package trabajofinalalgoritmica2;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class TrabajoFinalAlgoritmica2 {

    private static final int NUMMEDICOS = 7; //Número de Médicos elegibles.
    private static int NUMPACIENTES = 35; //Número de pacientes.
    private static Doctor[] listaMedicos = new Doctor[NUMMEDICOS];
    private static Patient[] listaPacientes = new Patient[NUMPACIENTES];
    private static int numIterator = 1;
    
    public static void main(String[] args) {
        //Crear lista pacientes
        createListPatient();
        //Crear lista médicos.
        createListDoctor();
        
        //Ejecuto el algoritmo genético.
        long time_start_Ev, time_end_Ev;
        time_start_Ev = System.currentTimeMillis();
        double totalFitnessEv = 0;
        for(int i=0; i<numIterator; i++){
            AlgoritmoEvolutivo algEv = new AlgoritmoEvolutivo();
            totalFitnessEv += algEv.run(listaMedicos,listaPacientes);
        }
        time_end_Ev = System.currentTimeMillis();
        System.out.println("\n\n\n\n******************************************************************************************\n\n\n\n");
        
        //Ejecuto el algoritmo de enfriamiento simulado.
        long time_start_EnfSim, time_end_EnfSim;
        time_start_EnfSim = System.currentTimeMillis();
        double totalFitnessEnfSim = 0;
        for(int i=0; i<numIterator; i++){
            AlgoritmoEnfriamientoSimulado algEnfSim = new AlgoritmoEnfriamientoSimulado();
            totalFitnessEnfSim += algEnfSim.run(listaMedicos,listaPacientes);
        }
        time_end_EnfSim = System.currentTimeMillis();
        System.out.println("\n\n\n\n******************************************************************************************\n\n\n\n");
        
        //Ejecuto el algoritmo de enfriamiento simulado.
        long time_start_Mem, time_end_Mem;
        time_start_Mem = System.currentTimeMillis();
        double totalFitnessMem = 0;
        for(int i=0; i<numIterator; i++){
            AlgoritmoMemetico algMem = new AlgoritmoMemetico();
            totalFitnessMem += algMem.run(listaMedicos, listaPacientes);
        }
        time_end_Mem = System.currentTimeMillis();
        System.out.println("\n\n\n\n******************************************************************************************\n\n\n\n");
        
        System.out.println("DATOS DEL PROBLEMA:\nNúmero de Medicos = "+NUMMEDICOS+"\nNúmero de Pacientes = "+NUMPACIENTES+"\nNÚMERO de ejecuciones de cada Algoritmo = "+numIterator);
        System.out.println("\n\nRESULTADOS OBTENIDOS:");
        System.out.println("\nAlgoritmo Genético:                Fitness promedio = "+(totalFitnessEv/numIterator)+"     Tiempo de ejecución = "+ ((time_end_Ev - time_start_Ev)/numIterator) +" Milisegundos.");
        System.out.println("\nAlgoritmo Enfriamiento Simulado:   Fitness promedio = "+(totalFitnessEnfSim/numIterator)+"     Tiempo de ejecución = "+ ((time_end_EnfSim - time_start_EnfSim)/numIterator) +" Milisegundos.");
        System.out.println("\nAlgoritmo Memético:                Fitness promedio = "+(totalFitnessMem/numIterator)+"     Tiempo de ejecución = "+ ((time_end_Mem - time_start_Mem)/numIterator) +" Milisegundos.");
        System.out.println("");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Crea la lista de médicos.
    public static void createListDoctor(){
        for(int i=0; i<NUMMEDICOS; i++){
            listaMedicos[i] = new Doctor(createResidence(), i);
            
        }
    }
    
    //Crea la lista de pacientes.
    public static void createListPatient(){
        for(int i=0; i<NUMPACIENTES; i++){
            listaPacientes[i] = new Patient(createResidence(), i);
        }
    }
    
    //Devuelve un objeto Residence
    public static Residence createResidence(){
        int x = (int) (Math.random()*100);  // Valor x entre 0 y 99.
        int y = (int) (Math.random()*100);  // Valor y entre 0 y 99.
        Residence res = new Residence(x, y);
        return res;
    }
    
}
