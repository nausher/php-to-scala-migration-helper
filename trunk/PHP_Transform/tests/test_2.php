<?php
// adapted from http://www.php.happycodings.com/Algorithms/code33.html

// Function that calculates the roman string to the given number:

function dec2roman($f)
{
	// Return false if either $f is not a real number, $f is bigger than 3999 or $f is lower or equal to 0:
	if(!is_numeric($f) || $f > 3999 || $f <= 0) return false;

	// Define the roman figures:
	$roman = array('M' => 1000, 'D' => 500, 'C' => 100, 'L' => 50, 'X' => 10, 'V' => 5, 'I' => 1);

	$amount = array();

	// Calculate the needed roman figures:
	foreach($roman as $k => $v) if(($amount[$k] = floor($f / $v)) > 0) $f -= $amount[$k] * $v;

	// Build the string:
	$retval = '';
	foreach($amount as $k => $v)
	{
		$retval .= $v <= 3 ? str_repeat($k, $v) : $k . $old_k;
		$old_k = $k;
	}

	// Replace some spacial cases and return the string:
	return str_replace(array('VIV','LXL','DCD'), array('IX','XC','CM'), $retval);
}

// Function to get the decimal value of a roman string:
function roman2dec($str = '')
{
	// Return false if not at least one letter is in the string:
	if(is_numeric($str)) return false;

	// Define the roman figures:
	$roman = array('M' => 1000, 'D' => 500, 'C' => 100, 'L' => 50, 'X' => 10, 'V' => 5, 'I' => 1);

	// Convert the string to an array of roman values:
	for($i = 0; $i < strlen($str); $i++) if(isset($roman[strtoupper($str[$i])])) $values[] = $roman[strtoupper($str[$i])];

	// Calculate the sum of that array:
	$sum = 0;
	while($curr = current($values))
	{
		$next = next($values);
		$next > $curr ? $sum += $next - $curr + 0 * next($values) : $sum += $curr;
	}

	// Return the value:
	return $sum;
}

if(count($argv) > 2) {
    $ni = $argv[1];
    $nj = $argv[2];
    $dump = '';
    for($i=0; $i< $ni; ++$i) {
      for($j=0; $j< $nj; ++$j) {
        $dump .= dec2roman($j+0);
      }
    }
    print("Total bytes: " . strlen($dump));
}
else if(is_numeric($argv[1])) {
    print(dec2roman($argv[1]));
}
else {
    print(roman2dec($argv[1]));
}

?>


