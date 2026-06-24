document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registroForm");

    form.addEventListener("submit", function (e) {
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const sexo = document.getElementById("sexo").value;

        if (password !== confirmPassword) {
            e.preventDefault();
            alert("Las contraseñas no coinciden.");
            return;
        }

        if (sexo !== "F" && sexo !== "M") {
            e.preventDefault();
            alert("El sexo seleccionado no es válido (solo F o M).");
            return;
        }

        console.log("Validaciones web exitosas.");
    });
});