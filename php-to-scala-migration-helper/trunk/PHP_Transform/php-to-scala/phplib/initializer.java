package phplib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.ReaderStream;
import com.caucho.vfs.StdoutStream;
import com.caucho.vfs.StreamImpl;
import com.caucho.vfs.WriteStream;

public class initializer {

	private final QuercusContext quercus = new QuercusContext();
	private WriteStream out;

	public Env initialize(OutputStream os) {

		ReadStream reader = ReaderStream.open(new StringReader("1;"));
		QuercusProgram program;
		try {
			program = QuercusParser.parse(quercus, null, reader);
		} catch (IOException impossible) {
			throw new RuntimeException(impossible);
		}

		if (os != null) {
			OutputStreamStream s = new OutputStreamStream(os);
			WriteStream ws = new WriteStream(s);

			ws.setNewlineString("\n");

			try {
				ws.setEncoding("iso-8859-1");
			} catch (Exception e) {
			}

			out = ws;
		} else
			out = new WriteStream(StdoutStream.create());

		QuercusPage page = new InterpretedPage(program);
		
		Env quercus_context = new Env(quercus, page, out, null, null);
		quercus_context.start();
		
		return quercus_context;
	}

	class OutputStreamStream extends StreamImpl {
		OutputStream _out;

		OutputStreamStream(OutputStream out) {
			_out = out;
		}

		@Override
		public boolean canWrite() {
			return true;
		}

		@Override
		public void write(byte[] buffer, int offset, int length, boolean isEnd)
				throws IOException {
			_out.write(buffer, offset, length);
		}
	}

}