### Emotion Wheel App

### Antes de empezar

La app Emotion Wheel funciona apartir de la Realtime Database de firebase, para su correcto uso la linea 32 de la clase "FirebaseManager" debe ser editada al path de la llave de acceso en su ordenador.

![image](https://github.com/user-attachments/assets/44049ee4-0237-4bc9-9ad0-b94421ec337e)


## Estructura del Código

El código está organizado en tres paquetes principales: **Model**, **ViewModel** y **View**, cada uno con sus respectivas clases. Esta estructura sigue el patrón de diseño **MVVM (Model-View-ViewModel)**, que permite una separación clara de responsabilidades y facilita el mantenimiento y la escalabilidad del proyecto.

![image](https://github.com/user-attachments/assets/ab04f356-9936-43fe-a918-7071ef560cff)

## Funcionamiento del Código

### Autenticación

Al ejecutar la aplicación, se abre una ventana de autenticación donde el usuario puede iniciar sesión o registrarse en su cuenta. Además, en la esquina superior derecha, hay un botón de **"Temas"** que permite personalizar la interfaz según las preferencias del usuario. Es importante destacar que el rol asignado al usuario durante el registro no puede ser modificado posteriormente.

![image](https://github.com/user-attachments/assets/86d9bb36-1f86-42df-9b0e-ebc366173f0a) // ![image](https://github.com/user-attachments/assets/70f18851-be10-4528-956c-b67674657842)

### Interfaz de los Pacientes

Cuando un usuario inicia sesión con una cuenta de **paciente**, se despliega una interfaz centrada en la **Rueda de Plutchik**, que muestra los colores correspondientes a cada emoción. La aplicación utiliza la fuente **SheriffSans**, seleccionada por su neutralidad, ya que no influye en el estado emocional del usuario. Los fondos de la interfaz son de color **azul oscuro** en el tema oscuro y **blanco** en el tema claro, ambos elegidos por ser colores neutros que minimizan el impacto en el ánimo del usuario.

![image](https://github.com/user-attachments/assets/fb88266a-c339-45b7-8917-c883e1ff04af) // ![image](https://github.com/user-attachments/assets/6222ca6c-8216-4f82-be83-0332048a78e8)

Al seleccionar una emoción en la rueda, se le solicita al usuario que ingrese un comentario para aclarar cómo se siente en ese momento. Este comentario es crucial para un análisis más detallado de sus emociones.

![image](https://github.com/user-attachments/assets/6cd09452-50dd-458d-8dc1-f09061496180) // ![image](https://github.com/user-attachments/assets/1821a968-66f4-4487-b302-14873db34b1c)

Al presionar **"OK"**, los datos ingresados se guardan en la base de datos de **Firebase**, asociados al usuario correspondiente.

![image](https://github.com/user-attachments/assets/b7575f56-47a5-4361-95f1-747b46563b10)

En la parte izquierda de la interfaz, se encuentran botones que permiten realizar operaciones **CRUD (Crear, Leer, Actualizar, Eliminar)** sobre los registros emocionales. Estas funciones incluyen:

### Ver los Logs

El usuario puede visualizar todos sus registros emocionales almacenados, lo que le permite revisar su historial de emociones y comentarios.

![image](https://github.com/user-attachments/assets/78d23c2d-3cf2-451c-a06b-1ca2e0062e8a) // ![image](https://github.com/user-attachments/assets/e62b6550-e219-489b-bb49-635bb00ea4b7)

### Editar los Logs

El usuario tiene la posibilidad de modificar los comentarios asociados a sus emociones en caso de que desee actualizar la información.

![image](https://github.com/user-attachments/assets/400f1941-10ad-4095-9996-ea437f74eaf4) // ![image](https://github.com/user-attachments/assets/e632c4a8-b000-449c-8b2a-c75dbf2bd055)

### Eliminar Logs

El usuario puede eliminar registros específicos o borrar todos sus logs de emociones si lo considera necesario.

![image](https://github.com/user-attachments/assets/6264a92c-bc99-4b8d-96fc-0ddb9aaa9857) // ![image](https://github.com/user-attachments/assets/cc8374b7-0b97-4541-b041-fd3aac357cc3)

![image](https://github.com/user-attachments/assets/40028913-34cf-4548-9887-c6857eb1439f) // ![image](https://github.com/user-attachments/assets/83d7f370-bc81-43a7-8c10-bba961e7f7a3)

### Diagnóstico Provisional (Dado por OpenAI)

Al presionar el botón **"Diagnóstico Provisional"**, los datos del usuario se envían en formato **JSON** a una **IA entrenada** (OpenAI), que ha sido contextualizada para entender el propósito de la aplicación. La IA analiza la información y genera un diagnóstico provisional basado en los registros emocionales del usuario.

![image](https://github.com/user-attachments/assets/577a6b2d-b535-4ee5-8cf9-8bfbfc866c95)

![image](https://github.com/user-attachments/assets/7b04a9da-48b5-4322-89e1-9d6281905c48)

### Interfaz del Profesional

Cuando un **profesional** inicia sesión, se despliega un menú simple con las funciones disponibles para este tipo de usuario. La interfaz incluye una **barra de búsqueda** para encontrar pacientes por su nombre de usuario y un botón para enviar diagnósticos.

![image](https://github.com/user-attachments/assets/fcaeb7be-5740-46ac-a9dc-61985edc823a) // ![image](https://github.com/user-attachments/assets/c2fde78e-e50a-429c-9785-104b6742a8de)

Al buscar un usuario, todos sus registros emocionales aparecen en la mitad de la pantalla, proporcionando al profesional un contexto completo antes de emitir un diagnóstico.

![image](https://github.com/user-attachments/assets/d133258b-12d2-4f15-9ef6-cd0731fc986d) // ![image](https://github.com/user-attachments/assets/79c2e144-e1ca-420b-8f73-40cba848de49)

Al presionar el botón para **"Dar Diagnóstico"**, se abre un pop-up que solicita el nombre de usuario del paciente y el diagnóstico que se desea asignar. Una vez enviado, se muestra un mensaje de éxito y el diagnóstico se refleja en la interfaz del paciente.

![image](https://github.com/user-attachments/assets/e11013ab-3770-40c6-b0f2-c8ab5f34f82e) // ![image](https://github.com/user-attachments/assets/5c5b67c0-81bc-4a17-bbab-f58ab015d294)

![image](https://github.com/user-attachments/assets/3eeec2ce-e610-408a-b0af-33374a062a73)

![image](https://github.com/user-attachments/assets/d9be7e3e-bd36-4667-b02c-2ea11d6e219c)

## Implementación de la API de OpenAI para Diagnósticos Provisionales y Uso de Firebase

Para el funcionamiento de este proyecto, se integró la **API de OpenAI**, que permite generar diagnósticos provisionales basados en los datos emocionales del usuario. Para ello, se registró en OpenAI y se generó una clave de acceso a la API. Además, se utilizó **Firebase** para gestionar la autenticación de usuarios y almacenar los datos de manera segura y eficiente. Firebase facilita la preservación de los registros emocionales y permite una sincronización en tiempo real entre las diferentes interfaces de la aplicación.
