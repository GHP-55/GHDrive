<?php
$my_file = 'ip.txt';
if(isset($_REQUEST["ip"]))
{
	$handle = fopen($my_file, 'w') or die('Cannot open file:  '.$my_file);
	fwrite($handle, htmlentities($_REQUEST["ip"]));
	fclose($handle);
}
else
{
	$handle = fopen($my_file, 'r');
	$data = fread($handle,filesize($my_file));
	echo($data);
	fclose($handle);
}
