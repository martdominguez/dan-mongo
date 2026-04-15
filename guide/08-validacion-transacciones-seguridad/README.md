# Modulo 08: Validacion, transacciones y seguridad basica

## Objetivo

Incorporar mecanismos basicos para proteger la calidad del dato, decidir con criterio cuando usar transacciones y operar MongoDB de forma mas segura desde un backend.

## Que vas a aprender

- Por que MongoDB necesita validacion incluso cuando la aplicacion ya valida entradas.
- Como definir validacion de esquema a nivel de coleccion con reglas introductorias y utiles para backend.
- Que tipos de reglas conviene aplicar primero: campos obligatorios, tipos y restricciones basicas.
- Que es una transaccion en MongoDB y que problema intenta resolver.
- En que escenarios una transaccion ayuda y en cuales solo agrega costo operativo.
- Que papel cumplen las sesiones al ejecutar operaciones transaccionales.
- Por que un buen modelado suele reducir la necesidad de transacciones multi-documento.
- Que significan autenticacion y autorizacion en un entorno de aplicacion.
- Como aplicar el principio de menor privilegio para proteger datos y operaciones.
- Que errores comunes exponen una base de datos a cambios destructivos o accesos innecesarios.

## Enfoque del modulo

Hasta el modulo 07 trabajamos mucho sobre modelado, relaciones, duplicacion y consistencia. Ese contexto es clave para entender este modulo.

La idea central ahora es esta:

- validar ayuda a evitar que entren documentos mal formados
- transaccionar ayuda a coordinar cambios cuando una unidad de trabajo abarca varios documentos
- asegurar accesos ayuda a evitar que una aplicacion pueda hacer mas de lo que realmente necesita

Estos tres temas se relacionan, pero no significan lo mismo.

Por eso el modulo los separa con claridad:

1. validacion protege calidad estructural del dato
2. transacciones coordinan cambios
3. seguridad limita quien puede hacer que cosa

Vamos a trabajar con escenarios backend realistas:

- validacion de pedidos y pagos
- actualizaciones que tocan mas de una coleccion
- servicios que deben conectarse con permisos acotados
- operaciones que conviene blindar para no borrar o modificar datos por error

No vamos a profundizar en administracion avanzada de replica sets, despliegues empresariales ni internals de seguridad. La prioridad es desarrollar criterio tecnico aplicable desde una API o servicio backend.

## Archivos del modulo

- `theory.md`: conceptos, sintaxis, variaciones y criterios de decision.
- `examples.md`: ejemplos guiados de validacion, transacciones y seguridad a nivel de aplicacion.
- `exercises.md`: ejercicios de analisis, definicion de reglas y toma de decisiones.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- definir validaciones introductorias para una coleccion con foco en calidad de datos
- distinguir entre validacion estructural y reglas de negocio de aplicacion
- justificar cuando una transaccion aporta valor y cuando es innecesaria
- explicar que es una sesion en MongoDB en el contexto de transacciones
- detectar permisos excesivos en una aplicacion backend
- proponer accesos mas seguros para servicios que usan MongoDB en produccion
- reducir el riesgo de operaciones destructivas accidentales
