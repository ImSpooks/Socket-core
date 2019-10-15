<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style>
        body {
            background-color: #37383e;
            color: #dadada;
        }
    </style>
</head>
<body>
    Test 2?<br>

    <?php

    /*
     * NOTE: Current build isnt testable, because it isnt finished and developed with Php 7.4 that is still in beta
     */

    require_once "client/Test.php";

    new Test();

    //printf("\nUsing \\parallel\\Runtime is %s\n", $future->value());
    ?>
</body>
</html>