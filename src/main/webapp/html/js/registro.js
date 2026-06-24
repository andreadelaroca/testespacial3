document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registroForm');
    const mensaje = document.getElementById('registroMensaje');

    if (!form || !mensaje) return;

    const setMessage = (text, isError = false) => {
        mensaje.textContent = text;
        mensaje.style.color = isError ? '#b91c1c' : '#006b94';
    };

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const nombres = String(formData.get('nombres') || '').trim();
        const apellidos = String(formData.get('apellidos') || '').trim();
        const email = String(formData.get('email') || '').trim();
        const username = String(formData.get('username') || '').trim();
        const fechaNac = String(formData.get('fechaNac') || '').trim();
        const sexo = String(formData.get('sexo') || '').trim();
        const profesion = String(formData.get('profesion') || '').trim();
        const estudios = String(formData.get('estudios') || '').trim();
        const password = String(formData.get('password') || '').trim();
        const confirmPassword = String(formData.get('confirmPassword') || '').trim();
        const sexoNormalizado = sexo.toUpperCase();

        if (!nombres || !apellidos || !email || !username || !fechaNac || !sexo || !profesion || !estudios || !password) {
            setMessage('Completa todos los campos obligatorios.', true);
            return;
        }

        if (password.length < 8) {
            setMessage('La contraseña debe tener al menos 8 caracteres.', true);
            return;
        }

        if (password !== confirmPassword) {
            setMessage('Las contraseñas no coinciden.', true);
            return;
        }

        if (sexoNormalizado !== 'F' && sexoNormalizado !== 'M') {
            setMessage('El sexo solo puede ser F o M.', true);
            return;
        }

        setMessage('Registrando sujeto...');

        const body = new URLSearchParams({
            nombres,
            apellidos,
            email,
            username,
            fechaNac,
            sexo: sexoNormalizado,
            profesion,
            estudios,
            password
        });

        try {
            const response = await fetch('/testespacial3/api/registro-sujeto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                },
                body: body.toString()
            });

            const data = await response.json();

            if (!response.ok || !data.ok) {
                setMessage(data.message || 'No se pudo registrar el sujeto.', true);
                return;
            }

            setMessage('Registro completado. Ya puedes iniciar sesión.');
            window.location.href = '/testespacial3/html/login.html';
        } catch (error) {
            setMessage('Error de conexión al guardar el registro.', true);
        }
    });
});