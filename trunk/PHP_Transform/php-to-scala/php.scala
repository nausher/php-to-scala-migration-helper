/*
 * Scala/Quercus integration layer.
 * See http://code.google.com/p/php-to-scala-migration-helper/ for details.
 *
 * Copyright(C) 2010 Alex T. Ramos / Zigabyte Corporation.
 * COPYING is permitted under the terms of the GNU General Public License, v3.
 *
 * $Id: php.scala,v 1.16 2010-04-29 04:02:33 alex Exp $
 */

package php

import phplib._;
import com.caucho.quercus.env._;
import scala.collection.mutable.Map;

object quercus_context extends initializer {
	lazy val env = initialize(System.out);
}

/**
 * catch-all class capable of holding a PHP value.
 * It relies heavily on the Quercus Value class, but adds a layer of Scala-oriented convenience.
 */
abstract class ref {

  def value: com.caucho.quercus.env.Value

  /**
   * Exception for operations that are only supported on subclassed types (Int, Obj ,etc).
   */
  def wrong_type = throw new Exception("incorrect dynamic type for op: " + this.getClass)

  // dynamic object operators
  def ~&(sym: Symbol): method_ref = wrong_type
  def ~>(sym: Symbol): ref = wrong_type
  def update(sym: Symbol, new_val: ref): ref = wrong_type

  // string operations
  def +=&(o: ref): ref = wrong_type;
  def +&(o: ref): String = {
    this.toString + o.toString;
  }

  // array operations
  def apply(index: ref) : ref = wrong_type
  def update(index: ref, new_val: ref): ref = wrong_type
  def foreach[R](f: (ref,ref) => R) { wrong_type }
  def foreach[R](f: (ref) => R) { wrong_type }

  // scalar operations
  def ++() : ref = wrong_type
  def -=(o: ref): ref = wrong_type // not possible from here
  def +=(o: ref): ref = wrong_type
  def +(o: ref) = value.add(o.value)
  def *(o: ref) = value.mul(o.value)
  def +(i: Int) = value.toInt + i
  def *(i: Int) = value.toInt * i
  def /(i: Int) = value.toInt / i
  def >(o: ref) = value.gt(o.value)
  def <(o: ref) = value.lt(o.value)
  def <=(o: ref) = value.leq(o.value)
  def ==(o: ref) : Boolean = value.eq(o.value);

  // explicit conversions
  def toBoolean = this.value.toBoolean
  def toInt = this.value.toInt;
  def toDouble = this.value.toDouble;
  override def toString = value.toString
}

private[php] class quercus_ref(var v: Value) extends ref {
  override def value = v;
  override def -=(o: ref): ref = { this.v = this.v.sub(o.value); this }
  override def +=(o: ref): ref = { this.v = this.v.add(o.value); this }
}
private[php] object quercus_ref {
	def apply(a: Any): quercus_ref = {
		a match {
			case i: Integer => new quercus_ref(new LongValue(i.asInstanceOf[Long]));
			case d: Double => new quercus_ref(new DoubleValue(d));
			case s: String => new quercus_ref(new ConstStringValue(s));
			case qr: quercus_ref => qr;
			case r: ref => new quercus_ref(r.value);
			case v: Value => new quercus_ref(v);
			case a: AnyRef => throw new Exception("unknown: " + a.getClass)
		}
	}
}

class vnull extends ref {
  override def value = new ConstStringValue("");
}

class integer(var i: Int) extends ref {
  override def value = new LongValue(i)
  override def -=(x: ref): ref = { this.i = this.i - x.toInt; this }
  override def +=(x: ref): ref = { this.i = this.i + x.toInt; this }
  override def ++(): ref = { this.i = this.i + 1; this } /* due to Scala quirk, cannot write this.i++ here */
}

class string(s: String) extends ref {
  private var str = new StringBuffer(s)
  override def value = new ConstStringValue(str.toString)
  override def -=(x: ref): ref = { this.str = new StringBuffer(this.toInt - x.toInt); this }
  override def +=(x: ref): ref = { this.str = new StringBuffer(this.toInt + x.toInt); this }
  override def +=&(x: ref): ref = { str.append(x.toString); this }
  override def toString = str.toString
}

class array(var vec: ArrayValue) extends ref {
	override def value = vec
	private def toMap: Map[Value,Value] = {
		var m = this.value.toJavaMap(quercus_context.env, classOf[java.util.LinkedHashMap[ref,ref]]);
		var n = m.asInstanceOf[java.util.LinkedHashMap[Value,Value]]
		return scala.collection.JavaConversions.asMap(n);
	}
	override def foreach[R](f: (ref,ref) => R) {
		val it = this.value.getIterator(quercus_context.env);
		while(it.hasNext) {
			val e = it.next;
			val k = e.getKey();
			val v = e.getValue();

			f(quercus_ref(k), quercus_ref(v));
		}
		//this.toMap.foreach ( t => { f(new quercus_ref(t._1), new quercus_ref(t._2)); } );
	}
	override def update(index: ref, new_val: ref): ref = {
		vec.put(index.value, new_val.value);
		return new_val;
	}
	override def apply(index: ref) : ref = {
		return new quercus_ref(vec.get(index.value));
	}
	override def +=(v: ref): ref = {
		this.value.put(v.value);
		return v;
	}
}

object array {

  def list(elem: ref*): ref = {
    var av = new ArrayValueImpl();
    elem.foreach { x: ref => av.put(x.value) };
    return new array(av);
  }

	def map(kvpair: (ref,ref)*): ref= {
    var av = new ArrayValueImpl();
		for ((k,v) <- kvpair) { av.put(k.value, v.value) };
		return new array(av);
	}
}

class method_ref(o: AnyRef, m: java.lang.reflect.Method) {
  private def objectize(a: AnyRef): ref = {
    a match {
      case php: ref => return php;
      case i: Integer => return new integer(i.intValue);
      case other: AnyRef => throw new Exception("unhandled: " + other.getClass);
    }
  }

  def ~>(args: Any*): ref = {
    var a: Array[AnyRef] = args.toArray.map {a: Any => objectize(a.asInstanceOf[AnyRef])};
    return callRef(a: _*);
  }

  private def callRef(args: AnyRef*): ref = {
    var oa: Array[Object] = args.toArray.map {_.asInstanceOf[Object]};
    //println("calling: " + m.toString + " with: " + oa.length + " args");

    m.invoke(o, oa: _*) match {
      case x: ref => x;
      case u => throw new Exception("unknown result" + u);
    }
  }
}


class obj extends ref {
  override def value = throw new Exception("objects do not have a value")

  override def ~&(sym: Symbol): method_ref = {
    val s = sym.toString.substring(1)

    var m = for{i <- this.getClass.getDeclaredMethods() if i.getName == s
    } yield i;
    if (m.length > 0) return new method_ref(this, m(0));
    throw new Exception("object class " + this.getClass + " no method " + s)
  }

  override def ~>(sym: Symbol): ref = {
    val s = sym.toString.substring(1)
    val f = this.getClass.getDeclaredField(s);
    f.setAccessible(true);
    val v = f.get(this);
    //println("Value of " + s + " = " + v);
    v match {
      case o: ref => return o;
      case u => throw new Exception("unknown result" + u);
    }
    return new string(v.toString);
  }

  override def update(sym: Symbol, new_val: ref): ref = {
    val s = sym.toString.substring(1)
    val f = this.getClass.getDeclaredField(s);
    f.setAccessible(true);
    f.set(this, new_val);
    //println("Value of " + s + " set to " + new_val);
    return new_val;
  }

  override def toString = "object[" + this.getClass + "]"
}

class refbool(b: Boolean) extends ref {
  override def value = BooleanValue.create(b);
}

abstract class script extends phplib(quercus_context.env) {
  def include;

  val undef: ref = new vnull;

  var argv = array.list(this.toString);

  def main(args: Array[String]) {
    args.foreach { argv += _ }
    this.include;
  }

  implicit def stringToPhp(s: String): ref = new string(s)
  implicit def stringToQuercus(s: String): StringValue = new ConstStringValue(s)
  implicit def intToQuercus(i: Int): LongValue = new LongValue(i)
  implicit def quercusToInt(v: Value): Int = v.value.toInt
  implicit def intToPhp(i: Int): integer = new integer(i)
  implicit def phpToInt(a: ref): Int = a.toInt
  implicit def phpToPhpInt(a: ref): integer = a.toInt
  implicit def booleanToPhp(b: Boolean): ref = new refbool(b)
  implicit def phpToBoolean(a: ref): Boolean = a.toBoolean
  implicit def quercusToPhp(v: Value): ref = new quercus_ref(v)
  implicit def phpToStringValue(a: ref): StringValue = a.value.toString(quercus_context.env)

  def anyToQuercus(a: Any): ref = {
	  a match {
	 	  case i: Int => i;
	 	  case s: String => s;
	 	  case v: Value => v;
	  }
  }

  final class phpArrowAssoc[A](val x: A) {
    @inline def -> [B](y: B): Tuple2[ref,ref] = Tuple2(anyToQuercus(x), anyToQuercus(y))
  }
  implicit def any2ArrowAssoc[A](x: A): phpArrowAssoc[A] = new phpArrowAssoc[A](x)

  class ternary_condition(val b:Boolean) {
	  def |?[A](f: Boolean => A) : A = f(b)
  }
  implicit def boolean2ternary_condition(b: Boolean) : ternary_condition = {
		  new ternary_condition(b)
  }
  implicit def php2ternary_condition(b: ref) : ternary_condition = {
		  new ternary_condition(b.toBoolean)
  }

  def printf(f: String, args: ref*) {
    var values = args.map {arg => arg.value};
    Predef.print(sprintf(f, values.toArray));
  }

  def echo(s: String) = Predef.print(s)
  def echo(s: ref) = Predef.print(s.toString)
  
  def isset(v: ref) = v.value.isset
  def current(v: ref) = v.value.current
  def next(v: ref) = v.value.next
  def count(v: ref): ref = new quercus_ref(new LongValue(count(v.value, 0)))

}
