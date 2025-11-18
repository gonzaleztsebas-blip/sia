/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sia.sia.business.*;

/**
 *
 * @author luzel
 */
public class AdminMenu {

    public static void show() {
        Scanner scan = new Scanner(System.in);

        boolean running = true;

        while (running) {

            System.out.println("==== MENU ADMIN ====");
            System.out.println("1. Gestionar Estudiantes");
            System.out.println("2. Gestionar Profesores");
            System.out.println("3. Gestionar Cursos");
            System.out.println("4. Gestionar Grupos");
            System.out.println("5. Inscribir Estudiante");
            System.out.println("6. Registrar Notas");
            System.out.println("0. Salir");

            int option = scan.nextInt();
            scan.nextLine(); // limpiar ENTER

            switch (option) {

                case 1:
                    System.out.println("==== GESTIONAR ESTUDIANTES ====");
                    System.out.println("1. Crear Estudiante");
                    System.out.println("2. Actualizar Estudiantes");
                    System.out.println("3. Encontrar Estudiante");
                    System.out.println("4. Ver Estudiantes");
                    System.out.println("5. Borrar Estdudiante");
                    System.out.println("6. Registrar Notas");
                    System.out.println("0. Salir");

                    int optS = scan.nextInt();
                    scan.nextLine();

                    switch (optS) {

                        case 1:                        
                            
                            System.out.print("Usuario: ");
                            String user11 = scan.nextLine();
                            
                            System.out.print("Password: ");
                            String password11 = scan.nextLine();
                            

                            System.out.print("Nombre: ");
                            String name11 = scan.nextLine();

                            System.out.print("Apellido: ");
                            String last11 = scan.nextLine();

                            System.out.print("Nacimiento: ");
                            String birth11 = scan.nextLine();

                            StudentManager.createStudent(user11, password11, name11, last11, birth11);
                            break;
                        
                        case 2:
                            
                            System.out.print("Usuario: ");
                            String user12 = scan.nextLine();
                            
                            System.out.print("Nombre: ");
                            String name12 = scan.nextLine();

                            System.out.print("Apellido: ");
                            String last12 = scan.nextLine();
                            
                            StudentManager.updateStudent(user12,name12, last12);
                            break;
                            
                        case 3:
                            
                            System.out.print("Usuario: ");
                            String user13 = scan.nextLine();                            
                            
                            StudentManager.printFindStudent(user13);
                            break;
                            
                        case 4:
                            
                            StudentManager.listStudents();
                            break;

                        case 5:
                            
                            System.out.print("Usuario a borrar: ");
                            String toDelete = scan.nextLine();

                            StudentManager.deleteStudent(toDelete);
                            break;
                    }
                    break;

                    
                case 2:
                    System.out.println("==== GESTIONAR PROFESORES ====");
                    System.out.println("1. Crear Profesor");
                    System.out.println("2. Actualizar Profesor");
                    System.out.println("3. Encontrar Profesor");
                    System.out.println("4. Ver Profesores");
                    System.out.println("5. Borrar Profesor");
                    System.out.println("6. Asignar Profesor a Grupo");
                    System.out.println("0. Volver");

                    int optP = scan.nextInt();
                    scan.nextLine();

                    switch (optP) {
                        
                        case 1:
                            
                            System.out.print("Usuario: ");
                            String user11 = scan.nextLine();
                            
                            System.out.print("Password: ");
                            String password11 = scan.nextLine();
                            

                            System.out.print("Nombre: ");
                            String name11 = scan.nextLine();

                            System.out.print("Apellido: ");
                            String last11 = scan.nextLine();

                            System.out.print("Nacimiento: ");
                            String birth11 = scan.nextLine();

                            ProfessorManager.createProfessor(user11, password11, name11, last11, birth11);
                            break;

                        case 2:
                            
                            System.out.print("Usuario: ");
                            String user12 = scan.nextLine();
                            
                            System.out.print("Nombre: ");
                            String name12 = scan.nextLine();

                            System.out.print("Apellido: ");
                            String last12 = scan.nextLine();
                            
                            ProfessorManager.updateProfessor(user12,name12, last12);
                            break;
                            
                        case 3:
                            
                            System.out.print("Usuario: ");
                            String user13 = scan.nextLine();                            
                            
                            ProfessorManager.findProfessor(user13);
                            break;

                        case 4:
                            
                            ProfessorManager.listProfessors();
                            break;

                        case 5:
                            System.out.print("Usuario a borrar: ");
                            String toDelete = scan.nextLine();

                            ProfessorManager.deleteProfessor(toDelete);
                            break;

                        case 6:
                            // -> TeacherManager.assignTeacherToGroup(...)
                            break;
                    }
                    break;



                case 3:
                    System.out.println("==== GESTIONAR CURSOS ====");
                    System.out.println("1. Crear Curso");
                    System.out.println("2. Actualizar Curso");
                    System.out.println("3. Encontrar Curso");
                    System.out.println("4. Ver Cursos");
                    System.out.println("5. Borrar Curso");
                    System.out.println("6. Agregar Requisito");
                    System.out.println("7. Ver Requisitos");
                    System.out.println("0. Volver");

                    int opC = Integer.parseInt(scan.nextLine());

                    switch (opC) {

                        case 1:
                            System.out.print("Nombre del curso: ");
                            String nameC = scan.nextLine();

                            System.out.print("Créditos: ");
                            int credits = Integer.parseInt(scan.nextLine());

                            System.out.print("Requisitos (separados por coma): ");
                            String reqLine = scan.nextLine();

                            List<String> reqs = new ArrayList<>();
                            if (!reqLine.isBlank()) {
                                for (String r : reqLine.split(";")) reqs.add(r.trim());
                            }

                            CourseManager.createCourse(nameC, credits, reqs);
                            break;

                        case 2:
                            System.out.print("Código del curso: ");
                            long codeU = Long.parseLong(scan.nextLine());

                            System.out.print("Nuevo nombre: ");
                            String newName = scan.nextLine();

                            System.out.print("Nuevos créditos: ");
                            int newCredits = Integer.parseInt(scan.nextLine());

                            System.out.print("Nuevos requisitos: ");
                            String reqU = scan.nextLine();

                            List<String> reqsU = new ArrayList<>();
                            if (!reqU.isBlank()) {
                                for (String r : reqU.split(";")) reqsU.add(r.trim());
                            }

                            CourseManager.updateCourse(codeU, newName, newCredits, reqsU);
                            break;

                        case 3:
                            System.out.print("Nombre del curso: ");
                            String searchName = scan.nextLine();

                            var c1 = CourseManager.findCourse(searchName);
                            System.out.println(c1 == null ? "No existe." : c1);
                            break;

                        case 4:
                            CourseManager.listCourses();
                            break;

                        case 5:
                            System.out.print("Código del curso a borrar: ");
                            long delC = Long.parseLong(scan.nextLine());

                            CourseManager.deleteCourse(delC);
                            break;

                        case 6:
                            System.out.print("Código del curso: ");
                            long codeR = Long.parseLong(scan.nextLine());

                            System.out.print("Nuevo requisito: ");
                            String reqAdd = scan.nextLine();

                            CourseManager.addRequisite(codeR, reqAdd);
                            break;

                        case 7:
                            System.out.print("Código del curso: ");
                            String codeReqList = scan.nextLine();

                            CourseManager.listRequisites(codeReqList);
                            break;
                    }
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    running = false;
                    break;

                default:
                    System.out.println("Opción inválida, intenta de nuevo.");
            }
        }

        scan.close();
    }
}
