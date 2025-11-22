# Sistema de Informacion Academico (SIA)
## Universidad Nacional de Colombia

---

## Descripcion del Proyecto

Sistema de gestion academica inspirado en el SIA de la Universidad Nacional de Colombia (2008-2015). 
Permite la gestion completa de estudiantes, profesores, cursos, grupos, inscripciones y calificaciones.

---

## Caracteristicas Principales

### Gestion de Usuarios
- Estudiantes
- Profesores  
- Administradores

### Gestion Academica
- Cursos con requisitos
- Grupos con horarios
- Inscripciones con validaciones
- Calificaciones y promedios

### Validaciones Implementadas
- Requisitos de cursos
- Cupos disponibles
- Cruces de horarios
- Limite de creditos por semestre
- Cursos ya aprobados
- Inscripciones duplicadas

---

## Estructura del Proyecto

```
sia.sia/
├── data/                    # Clases del modelo
│   ├── User.java           # Clase abstracta base
│   ├── Student.java        # Estudiante
│   ├── Professor.java      # Profesor
│   ├── Admin.java          # Administrador
│   ├── Course.java         # Curso
│   ├── Group.java          # Grupo
│   └── Grade.java          # Calificacion
│
├── business/               # Logica de negocio
│   ├── CSVManager.java    # Login y registro
│   ├── StudentManager.java
│   ├── ProfessorManager.java
│   ├── CourseManager.java
│   ├── GroupManager.java
│   ├── EnrollmentManager.java
│   ├── GradeManager.java
│   ├── ScheduleValidator.java
│   └── CodeNumbersManager.java
|
├── ui/                     # Interfaces de usuario
│   ├── AdminMenu.java
│   ├── StudentMenu.java
│   ├── ProfessorMenu.java
│   ├── StudentManagementMenu.java
│   ├── ProfessorManagementMenu.java
│   ├── CourseManagementMenu.java
│   ├── GroupManagementMenu.java
│   ├── EnrollmentMenu.java
│   └── GradeMenu.java
│
├── test/                   # Pruebas
│   ├── SystemTest.java
│   ├── EnrollmentTest.java
│   └── ScheduleValidationTest.java
│
├── Main.java              # Punto de entrada
└── QuickStartGuide.java   # Configuracion inicial
```

---

## Requisitos del Sistema

- Java JDK 11 o superior
- NetBeans IDE (recomendado)
- Libreria OpenCSV

---

## Instalacion

1. Clonar o descargar el proyecto
2. Abrir en NetBeans
3. Agregar libreria OpenCSV al proyecto
4. Ejecutar `QuickStartGuide.java` para configurar datos iniciales

---

## Archivos CSV Necesarios


```
usersCSV.csv
studentCSV.csv
professorCSV.csv
adminCSV.csv
courseCSV.csv
groupCSV.csv
gradeCSV.csv
enrollmentCSV.csv
idsGenerated.txt
codesGenerated.txt
groupsGenerated.txt
```

---

## Inicio Rapido

### Opcion 1: Configuracion automatica

```java
// Ejecutar QuickStartGuide.java
// Esto creara usuarios y datos de ejemplo
QuickStartGuide.main(new String[]{});
```

### Opcion 2: Inicio manual

```java
// Ejecutar Main.java
// Registrar usuarios manualmente
Main.main(new String[]{});
```

---

## Credenciales de Ejemplo

Despues de ejecutar QuickStartGuide:

**Administrador:**
- Usuario: `admin`
- Contrasena: `admin123`

**Profesores:**
- Usuario: `prof_garcia`
- Contrasena: `prof123`

**Estudiantes:**
- Usuario: `juan_perez`
- Contrasena: `student123`

---

## Funcionalidades por Rol

### Administrador
- Gestionar estudiantes, profesores y cursos
- Crear y modificar grupos
- Asignar profesores a grupos
- Inscribir estudiantes
- Registrar calificaciones
- Ver reportes completos

### Profesor
- Ver grupos asignados
- Ver lista de estudiantes
- Registrar calificaciones
- Actualizar calificaciones
- Ver promedios de grupo
- Consultar horarios

### Estudiante
- Ver historial academico
- Inscribir materias
- Retirar materias
- Ver horario actual
- Ver promedio general
- Ver creditos aprobados
- Consultar cursos disponibles

---

## Validaciones del Sistema

### Inscripcion de Materias

El sistema valida automaticamente:

1. **Requisitos**: El estudiante debe haber aprobado los requisitos
2. **Cupos**: El grupo debe tener cupos disponibles (max 40)
3. **Horarios**: No puede haber cruce de horarios
4. **Creditos**: No puede exceder 20 creditos por semestre
5. **Duplicados**: No puede inscribir el mismo grupo dos veces
6. **Aprobados**: No puede inscribir cursos ya aprobados

### Formato de Horarios

```
Dias: L, M, W, J, V, S, D
Horas: 7-9, 9-11, 11-13, 14-16, 16-18
Ejemplo: L;W;V con 7-9;7-9;7-9
```

---

## Ejecucion de Pruebas

### Test Principal
```java
SystemTest.main(new String[]{});
```

### Test de Inscripciones
```java
EnrollmentTest.main(new String[]{});
```

### Test de Horarios
```java
ScheduleValidationTest.main(new String[]{});
```

---

## Notas Importantes

1. **Persistencia**: Los datos se guardan en archivos CSV
2. **IDs Unicos**: El sistema genera IDs automaticamente
3. **Validacion**: Todas las operaciones criticas estan validadas

---

## Estructura de Datos CSV

### usersCSV.csv
```
username,password,role
```

### studentCSV.csv
```
username,password,role,id,firstName,lastName,birthDate,attends
```

### courseCSV.csv
```
code,name,credits,requisites
```

### groupCSV.csv
```
number,daysOfWeek,timesOfDay,semester,courseCode,professor,students,grades
```

### gradeCSV.csv
```
studentUser,groupNumber,grade
```

### enrollmentCSV.csv
```
studentUser,groupNumber,semester,enrollmentDate,status
```

---

## Autores

- Miguel Angel Sanchez Sandoval, Sebastian Gonzalez Torres, Juan David Rincon Lizarazo
- Proyecto desarrollado para el curso de Programacion Orientada a Objetos
- Universidad Nacional de Colombia
- 2025

---

## Licencia

Proyecto academico - Universidad Nacional de Colombia
