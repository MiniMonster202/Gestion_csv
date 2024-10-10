/* Programa:
- Agregar estudiantes (instanciar nuevos)
- Quitar estudiante del sistema
- Mostrar lista de todos los estudiantes y sus atributos
- Buscar por NIA (atributo) y mostrar sus datos
- Asociar estudiante a asigantura
- Quitar estudiante de asignatura

Tras usar metodos que modifican datos, actualizar el documento csv a parte de la lista
(los que pone CSV despues de la explicacion) COMO SE HACE? NECESARIO? 
FUNCION QUE SE DEDIQUE SOLO A ACTUALIZAR, LLAMARLA DANDOLE LA LISTA

clase estudiante
clase asignatura
clase registro
clase registroapp */

// IMPORTAR PARA LEER Y ESCRIBIR DE TECLADO Y FICHERO CSV Y LAS LISTAS
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList; 
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.channels.FileChannel;

public class RegisterApp{
    public static void main(String[]args){
        Scanner scanner= new Scanner(System.in);
    	System.out.println("\nIntroduce el nombre del fichero csv de estudiantes con extension:");
        String Studentsinput = scanner.nextLine();
        System.out.println("\nIntoduce el nombre del fichero csv de asignaturas con extension:");
        String Subjectsinput = scanner.nextLine();
        Register Registro = new Register(Studentsinput, Subjectsinput);
        Inicio.inicio(Registro);
    }
}
class Inicio{
    public static void inicio(Register Registro){
        Scanner scanner= new Scanner(System.in);
        // Metodo que lea de entrada y ya ejecute lo que se pide, invocando a los otros metodos
        System.out.println("\nEscriba cual de las siguientes tareas quiere realizar (1,2,3,4,5,6 o quit para salir): ");
        System.out.println("\n1) agregar estudiantes;                                  2) mostrar todos los estudiantes;");
        System.out.println("\n3) buscar un estudiante por su NIA;                      4) matricular estudiante en una asignatura;");
        System.out.println("\n5) desmatricular estudiante de una asignatura;           6) eliminar a un estudiante del sistema;\n");
        String entrada = scanner.nextLine();
        switch(entrada){
            case "1":
                Registro.add(scanner);
                break;
            case "2":
                Registro.show();
                break;
            case "3": 
                Registro.search(scanner);
                break;
            case "4":
                Registro.register(scanner);
                break;
            case "5":
                Registro.deregister(scanner);
                break;
            case "6": 
                Registro.delete(scanner);
                break;
            case "quit":
                System.out.println("\nFue un placer, vuelva pronto\n");
                System.exit(0);
                break;
            default:
                System.out.println("\nLa tarea escrita no es valida");
                Inicio.inicio(Registro);
            break;
        }
        Inicio.inicio(Registro);
    }
}
class Register {

    // ATRIBUTOS transformar csv en listas de java para poder usar los metodos de busqueda definidos para las listas :)
    // lista de objetos de la clase estudiante
    protected static List<Student> Studentslist  = new ArrayList<>();
    // lista de objetos de la clase asignaturas
    protected static List<Subject> Subjectslist  = new ArrayList<>();
    protected String Studentsinput, Subjectsinput, headerst, headersub;
    protected static String[] emptydata = {"null", "null", "null"};
    protected static Subject emptysubject = new Subject(emptydata);
    protected static Student emptystudent = new Student(emptydata);
    
    // CONSTRUCT
    public Register (String Studentsinput, String Subjectsinput){
    	// entrada fichero csv, dentro se transforma en listas y se ponen en los atributos
    	// Cada linea es un objeto de la clase estudiante/asignatura
    	// Queremos listas de estudiante/asignatura
        this.Studentsinput = Studentsinput;
        this.Subjectsinput = Subjectsinput;
        try (BufferedReader brsub = new BufferedReader(new FileReader(Subjectsinput))) {
            String lineasub;
            headersub = brsub.readLine();
            while ((lineasub = brsub.readLine()) != null) {
                // crear el objeto estudiante de cada linea y dar valor a los atributos con los elementos separador por comas
                String[] datossub = lineasub.split(",");
                Subject asignatura =new Subject (datossub); 
                // Anadir la asignatura a la lista de asignaturas
                 this.Subjectslist.add(asignatura); 
            }
        } catch (IOException e) {
            System.out.println("Ocurrio un error al leer el archivo: " + e.getMessage());}
    	try (BufferedReader brst = new BufferedReader(new FileReader(Studentsinput))) {
            String lineast;
            headerst = brst.readLine();
            while ((lineast = brst.readLine()) != null) {
                // crear el objeto estudiante de cada linea y dar valor a los atributos con los elementos separador por comas
                String[] datos = lineast.split(",");
                Student estudiante =new Student (datos); 
                // Anadir el Estudiante a la lista de estudiantes
                 this.Studentslist.add(estudiante); 
            }
        } catch (IOException e) {
        System.out.println("Ocurrio un error al leer el archivo: " + e.getMessage());}
    }
    
    // METODOS
    // Anadir estudiante CSV
	public void add(Scanner scanner) { // COMPLETO
		System.out.println("\nEscriba el nombre y apellido del estudiante: ");
		String nombre=scanner.nextLine();
        System.out.println("\ninserte las asignaturas en las que estará matriculado separando los códigos con un espacio: ");    
        String[] asignatura = scanner.nextLine().split(" ");
        String[] datos = new String[asignatura.length+2];
        datos[0] = nombre;
        datos[1] = NIAGenerator();
        for(int i=0;i<asignatura.length;i++){
            datos[i+2] = asignatura[i];
        }
        Student newstudent =new Student(datos);
        if(newstudent.registeredsubjects.contains(emptysubject)) System.out.println("\nAlguna de las asignaturas que has introducido no existe en el sistema, revisa el plan de estudios y vuelve a intentarlo");
        else{
            Studentslist.add(newstudent);
            updateCSV(Studentsinput);
            System.out.println("\nFelicidades "+nombre+" ya estás matriculado, este sera su NIA: "+datos[1]);
        }
	}
	// Eliminar estudiantes del CSV
	public void delete(Scanner scanner) { // COMPLETO
        System.out.println("\nEscriba el NIA del estudiante que desea eliminar del sistema: ");
        String expelledNIA = scanner.nextLine();
        Student expelledstudent = StudentNIA(expelledNIA);
        if(expelledstudent == emptystudent) System.out.println("\nEste estudiante no existe en el sistema.");
        else{
            System.out.println("\nEstudiante "+expelledstudent.name+"con NIA "+expelledstudent.nia+" va a ser eliminado del sistema");
            Studentslist.remove(expelledstudent);
            updateCSV(Studentsinput);
            System.out.println("\nActualización de la BDD completa");
        }
	}
	// Mostrar estudiantes
	public void show() { // COMPLETO
		for (Student currentstudent : Studentslist){
			currentstudent.Showinfo();
		}
	}
	// Buscar estudiante
	public void search(Scanner scanner) { // COMPLETO
		System.out.println("\nIntroduzca el nia del alumno que quiere encontrar: ");
		String currentnia = scanner.nextLine();
		Student currentstudent = StudentNIA(currentnia);
		if(currentstudent == emptystudent) System.out.println("\nEste estudiante no existe en el sistema.");
		else currentstudent.Showinfo();
	}
	// Matricular asignatura en CSV
	public void register(Scanner scanner) {// COMPLETO
		System.out.println("\nEscriba el nia del alumno al que quiere matricular: ");
		String currentNIA = scanner.nextLine();
		Student currentstudent = StudentNIA(currentNIA);
		if(currentstudent == emptystudent) System.out.println("\nEste estudiante no existe en el sistema.");
		else{
            System.out.println("\nEscriba el código de la asignatura en la que se va a matricular: ");
            String newsubject = scanner.nextLine();
            Subject newsub = SubjectCode(newsubject);
            if(newsub == emptysubject) System.out.println("\nEsta asignatura no existe en el sistema.");
            else{
                if(!currentstudent.registeredsubjects.contains(newsub)){ // si no está matriculado lo matricula
                    currentstudent.AddSubject(newsub);
                    updateCSV(Studentsinput);
                }
                else System.out.println("\nEste estudiante ya está matriculado en: "+newsub.code+" "+newsub.title);
            }
		}
	}
	// Desmatricular asignatura en CSV
	public void deregister(Scanner scanner) {
        System.out.println("\nEscriba el nia del alumno al que quiere desmatricular: ");
		String currentNIA = scanner.nextLine();
		Student currentstudent = StudentNIA(currentNIA);
		if(currentstudent == emptystudent) System.out.println("\nEste estudiante no existe en el sistema.");
		else{
            System.out.println("\nEscriba el código de la asignatura de la que se va a desmatricular: ");
            String oldsubject = scanner.nextLine();
            Subject oldsub = SubjectCode(oldsubject);
            if(oldsub == emptysubject) System.out.println("\nEsta asignatura no existe en el sistema.");
            else{
                if(currentstudent.registeredsubjects.contains(oldsub)){ // si está matriculado lo desmatricula
                    currentstudent.RemoveSubject(oldsub);
                    updateCSV(Studentsinput);
                }
                else System.out.println("\nEste estudiante no está matriculado en: "+oldsub.code+" "+oldsub.title);
            }
		}
	}
	public String NIAGenerator() { // Random NIA generator, no repetidos
        Random rand = new Random();
        int NIA = rand.nextInt(999999-100000+1)+100000; //generamos un NIA de 6 digitos 
        for(Student ex : Studentslist){
            if(NIA == Integer.parseInt(ex.nia)){
                break;
            }
            NIA++;
        }
        return Integer.toString(NIA);
	}
	// SubjectCode Devolver objeto Subject por codigo dado
	public  static Subject SubjectCode(String code) {//invocarlo solo desde esta clase para los metodos
        Subject foundsubject= emptysubject;
        //buscar en lista de asignaturas la que tenga ese codigo con metodos de listas
        for(Subject asig : Subjectslist){
            if(asig.code.equals(code)) {
                foundsubject=asig;
                break;
            }
        }
        return foundsubject;
	}
    // StudentNIA Devolver objeto Student por NIA dado 
	public  static Student StudentNIA(String nia) {//invocarlo solo desde esta clase para los metodos
        Student foundstudent = emptystudent;
        //buscar en lista de asignaturas la que tenga ese codigo con metodos de listas
        for(Student s : Studentslist){
            if(s.nia.equals(nia)) {
                foundstudent=s;
                break;
            }
        }
        return foundstudent;
	}
	// Actualizar documento CSV
	public void updateCSV(String fichero) { // Subjectslist y Studentslist para actualizar
        File ficherotemp = new File(fichero+".temp");
        try(BufferedWriter updatedCSV = new BufferedWriter(new FileWriter(ficherotemp))){
            if(fichero.equals(Studentsinput)){
                updatedCSV.write(headerst+"\n");
                for(Student s : Studentslist){
                    updatedCSV.write(s.name+","+s.nia);
                    for(Subject regsub : s.registeredsubjects){
                        updatedCSV.write(","+regsub.code);
                    }
                    updatedCSV.write("\n");
                    updatedCSV.flush(); // vamos borrando el buffer para ahorrar memoria
                }
            }
            else{
                updatedCSV.write(headersub+"\n");
                for(Subject s : Subjectslist){
                    updatedCSV.write(s.code+","+s.title+","+s.teacher);
                    updatedCSV.write("\n");
                    updatedCSV.flush(); // vamos borrando el buffer para ahorrar memoria
                }
            }
        } catch (IOException e) {
        System.out.println("Ocurrio un error al escribir el archivo: " + e.getMessage());}
        try {
            Files.move(ficherotemp.toPath(), Paths.get(fichero), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Ocurrio un error al reemplazar el archivo: " + e.getMessage());
        }
	}
}

class Student{ 

    // ATRIBUTOS
    protected String name,nia;
    protected List<Subject> registeredsubjects  = new ArrayList<>();

    // constructor recibe string de csv
	public Student(String[] datos){
        this.name=datos[0];
    	this.nia=datos[1];
    	for(int i=2;i<datos.length;i++){
    		this.registeredsubjects.add(Register.SubjectCode(datos[i]));
    	}
    }
	// Matricular asignatura en CSV
	public void AddSubject(Subject subj){
		if(subj==null) System.out.println("No existe esta asignatura");
		else{
            registeredsubjects.add(subj);
            System.out.println("\nAsignatura: "+subj.title+"; sumada al expediente de "+name+" "+nia);
        }
    }
	// Desmatricular asignatura en CSV
   public  void RemoveSubject(Subject subj){
        Subject oldsubject = Register.emptysubject;
        if(subj==null) System.out.println("\nNo existe esta asignatura");
        else{
            for(Subject regsubj : registeredsubjects){
                if(subj.equals(regsubj)) oldsubject = regsubj;
            }
            registeredsubjects.remove(oldsubject);
            System.out.println("\nAsignatura: "+subj.title+"; eliminada del expediente de "+name+" "+nia);
        }
    }
    // Mostrar datos de CSV
    public void Showinfo() {
    	System.out.println("\nEl estudiante "+ name+" con nia "+ nia + " tiene matriculadas las siguientes asignaturas: ");
    	for (Subject regsubj : registeredsubjects) {
    		System.out.println(regsubj.code+" - "+regsubj.title+" (Impartida por: "+regsubj.teacher+" )");
    	}
    }
}

class Subject{

    // ATRIBUTOS
    protected String code, title, teacher;
    
    // CONSTRUCT recibe string de csv
    public Subject(String[] datos){
    	this.code=datos[0];
    	this.title=datos[1];
    	this.teacher=datos[2];
    }
}
