<?php
// 1. Assign values to variables $a and $b and print them
$a = 10;
$b = 20;
echo "a = $a, b = $b<br>";

// 2. Assign the sum of $a and $b to $c and print it
$c = $a + $b;
echo "c = $c<br>";

// 3. Triple the value of $c and print the result
$c *= 3;
echo "c (after tripling) = $c<br>";

// 4. Divide $c by the difference of $b and $a and print the result
$c /= ($b - $a);
echo "c (after dividing by (b - a)) = $c<br>";

// 5. Create new variables $p and $b, assign values and print them
$p = "Program";
$b = "works";
echo "p = $p, b = $b<br>";

// 6. Concatenate the strings $p and $b, assign to $result and print it
$result = $p . ' ' . $b;
echo "result = $result<br>";

// 7. Append the word 'well' to $result using '.=' operator and print it
$result .= " well";
echo "result (after appending) = $result<br>";

// 8. Swap the values of $q and $w without using new variables and print them
$q = 5;
$w = 7;
list($q, $w) = array($w, $q);
echo "q = $q, w = $w<br>";
// list array
// 9. Loop to print numbers from 23 to 78
for ($i = 23; $i <= 78; $i++) {
    echo "$i ";
}
echo "<br>";

// 10. Loop to create an unordered list with 10 items
echo "<ul>";
for ($i = 1; $i <= 10; $i++) {
    echo "<li>Item $i</li>";
}
echo "</ul>";

// 11. Create an array of 100 random numbers and print it using while and foreach loops
$numbers = array();
for ($i = 0; $i < 100; $i++) {
    $numbers[] = rand(1, 100);
}

// Print array using while loop
$i = 0;
while ($i < count($numbers)) {
    echo $numbers[$i] . ' ';
    $i++;
}
echo "<br>";
echo "<br>";
// Print array using foreach loop
foreach ($numbers as $number) {
    echo $number . ' ';
}
echo "<br>";
echo "<br>";

// 12. Display a message depending on the day of the week using switch statement
$dayOfWeek = date('l');

// Using switch to determine the day of the week
switch ($dayOfWeek) {
    case 'Monday':
        echo "Today is Monday";
        break;
    case 'Tuesday':
        echo "Today is Tuesday";
        break;
    case 'Wednesday':
        echo "Today is Wednesday";
        break;
    case 'Thursday':
        echo "Today is Thursday";
        break;
    case 'Friday':
        echo "Today is Friday";
        break;
    case 'Saturday':
        echo "Today is Saturday";
        break;
    case 'Sunday':
        echo "Today is Sunday";
        break;
    default:
        echo "Unknown day";
}
echo "<br>";

// 13. Function to add 10 to a number and print the result
function getPlus10($number) {
    echo $number + 10 . "<br>";
}
echo "<br>";

getPlus10(5); // Example function call
?>