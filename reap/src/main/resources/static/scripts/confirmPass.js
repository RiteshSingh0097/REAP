$(document).ready(function () {
    $('#inputPassword').blur(function () {
        var password = document.getElementById("inputPassword")
            , confirm_password = document.getElementById("confirmpassword");
            console.log(password.value);
            console.log(confirm_password.value);
        if(password.value != confirm_password.value) {
            $('#err').text("Passwords Don't Match");
            $('#inputPassword').val("");
        } else {
            $('#err').text("");
        }
    })
});