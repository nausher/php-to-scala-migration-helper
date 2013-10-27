<?

$code = file_get_contents($argv[1]);

function escape($str) {
	return preg_replace("/[\r\n]+/", "\\n", $str);
}

foreach(token_get_all($code) as $c)
{
	if(is_array($c))
	{
		print(token_name($c[0]) . ": '" . escape($c[1]) . "'\n");
	}
	else
	{
		print("$c\n");
	}
}

?>