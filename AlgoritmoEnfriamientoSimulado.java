package trabajofinalalgoritmica2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class AlgoritmoEnfriamientoSimulado {
    
    private int numListDoctor; //Numero de médicos elegibles.
    private int numListPatient; //Numero de pacientes.
    private Patient[] listPatient; //Lista de pacientes
    private Doctor[] listDoctor;   //Lista de médicos
    private int[] bestSolutionSoFar = new int[numListPatient];
    private double minFitnessSoFar = Double.MAX_VALUE;
    
    //Variables para tunear el algoritmo.
    private double clienteRatio = 0.9;      //Importancia de satisfacción de los clientes.
    private double empresaRatio = 0.1;      //Importancia de satisfacción de la empresa.
    private double temp = 10000;             // Temperatura inicial
    private double coolingRate = 0.003;     //Tasa de enfriamiento, decrementando la temperatura
    
    public AlgoritmoEnfriamientoSimulado(){
        System.out.println("_-_ ALGORITMO ENFRIAMIENTO SIMULADO _-_");
    }
    
    public double run(Doctor[] d, Patient[] p) {
        listDoctor = d;
        listPatient = p;
        numListDoctor = d.length;
        numListPatient = p.length;
        //System.out.println("Total de medicos disponibles: " + numListDoctor);
        //System.out.println("Total de pacientes para asignar: " + numListPatient);
        int[] solution = new int[numListPatient];
        //Genero una solución inicial valida.
        boolean validate = false;
        //Mientras solución no valida vuelvo a crear una solución inicial.
        while(!validate){
            for (int i = 0; i < numListPatient; i++) {
                int selection = (int) (Math.random()*numListDoctor);  //Valores aleatorio 0, 9.
                solution[i] = selection;
            }
            if(fitness(solution)!=Double.MAX_VALUE){
                validate=true;
            }
        }
        
        //Actualizo solución inicial
        bestSolutionSoFar = solution;
        minFitnessSoFar = fitness(solution);
        //Muestro los resultados
        printResults();

        //Tasa de enfriamiento, decrementando la temperatura en 0.003)
        double coolingRate = 0.003;

        //Bucle hasta que el sistema se haya enfriado. Temperatura final es 1.
        while (temp > 1) {
            //System.out.println("\nTemperatura actual = "+temp);
            //Creo una solución vecina
            int[] newSolution = createNeighborhood(solution);
            
            //Calculo fitness de las soluciones.
            double currentEnergy = fitness(solution);  //coste de la nueva solucion
            double neighbourEnergy = fitness(newSolution);
            //System.out.println("FITNESS:  Solucion = "+currentEnergy+"    Vecino = "+neighbourEnergy);
            //Decide si debe aceptar al vecino.
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                System.arraycopy(newSolution, 0, solution, 0, solution.length);
                //System.out.println("\nACEPTO VECINO");
            }

            //Si mejora la mejor solución encontrada hasta el momento la actualiza.
            if (fitness(solution) < minFitnessSoFar) {
                System.arraycopy(solution, 0, bestSolutionSoFar, 0, bestSolutionSoFar.length);
                minFitnessSoFar = fitness(solution);
                //System.out.println("\nATENCIÓN!! MEJOR SOLUCIÓN ACTUALIZADA. "+minFitnessSoFar);
            }
            
            //Decremento la temperatura
            temp *= 1 - coolingRate;
        }
        printResults();
        return minFitnessSoFar;
    }
    
    //Calcula como de buena es la solución(A menor fitness mejor será la solución).
    private double fitness(int[] v) {
        double totalFitness = 0.0;
        double totalDistancia = 0.0;
        int totalSueldos = 0;
        //Array para contar cuales médicos aparecen en el individuo y cuantas veces.
        int[] medicosSelecionados = new int[numListDoctor];
        //Inicializo el array con ceros.
        for (int i = 0; i < numListDoctor; i++) {
            medicosSelecionados[i] = 0;
        }
        for (int i = 0; i < numListPatient; i++) {
            totalDistancia += listPatient[i].getAddress().calcularDistanciaDesde(listDoctor[v[i]].getDomicilio());
            //Contador de medicos seleccionados
            medicosSelecionados[v[i]]++;
            //Sumador de sueldos
            totalSueldos += listDoctor[v[i]].getSueldo();
        }
        //Regla fitness. Tuneable segun si la prioridad es la felicidad del cliente o el ahorro de costes para la empresa.
        totalFitness = ((totalDistancia/numListPatient)*clienteRatio) + ((totalSueldos/numListDoctor)*empresaRatio);
        
        //Si el numero de pacientes asignados a algun medico de la solución es mayor al permitido, ponemos el valor del fitness al máximo;
        for(int i=0; i<medicosSelecionados.length; i++){
            //Si el numero de pacientes asignados a algun medico de la solución es mayor al permitido, ponemos el valor del fitness al máximo;
            if(medicosSelecionados[i] > listDoctor[i].getNumMaxPacientes()){
                totalFitness = Double.MAX_VALUE;
            }
        }
        return totalFitness;
    }
    
    //Método crear vecino.
    private int[] createNeighborhood(int[] c) {
        int posChange;
        int aux;
        int newC[] = new int[c.length];
        //Copio la solución a la solución vecina.
        System.arraycopy(c, 0, newC, 0, c.length);
        //Crea solución vecina
        posChange = (int) (Math.random() * newC.length);
        //muta el gen. Si el valor del gen es 0 se suma 1. Si el valor del gen no es 0 se resta 1.
        aux = (int) (Math.random() * 2);
        if (newC[posChange] == 0) {
            newC[posChange]++;
        } else {
            newC[posChange]--;
        }
        
        return newC;
    }
    
    //Método probabilidad de aceptación.
    public static double acceptanceProbability(double energy, double newEnergy, double temperature) {
         //Si la nueva solución es mejor la acepto.
        if (newEnergy < energy) {
            return 1.0;
        }
        //Si la nueva solución es peor, calculo la probabilidad de aceptación.
        return Math.exp((energy - newEnergy) / temperature);
    }
    
    //Muestro las medicos seleccionados y sus pacientes asignados.
    public void muestraAsignaciones(){
        System.out.println("\nMUESTRO MEJOR ASIGNACIÓN ENCONTRADA:");
        List<Doctor> showDoctor = new ArrayList<>();
        //Creo la lista de medicos escogidos
        for(int i=0; i<bestSolutionSoFar.length; i++){
            //Si no está en la lista lo agrego.
            if(!showDoctor.contains( listDoctor[ bestSolutionSoFar[i] ]) ){
                Doctor d = new Doctor( listDoctor[bestSolutionSoFar[i]] );
                showDoctor.add(d);
            }
        }
        
        //Asigno los pacientes a los médicos
        for(int i=0; i<bestSolutionSoFar.length; i++){
            Patient p = new Patient( listPatient[i].getAddress(), listPatient[i].getCodPatient() );
            for(int j=0; j<showDoctor.size(); j++){
                //System.out.println("Doctor "+showDoctor.get(i).getCodMedico()+ "");
                if(bestSolutionSoFar[i]==showDoctor.get(j).getCodMedico()){
                    showDoctor.get(j).getListaPacientes().add(p);
                }
                
            }
        }
        
        // Ordeno los médicos usando lambda expressions de Java 8
        Collections.sort(showDoctor, (Doctor p1, Doctor p2) -> p2.getCodMedico()- p1.getCodMedico());
        Collections.reverse(showDoctor);
        //Los muestro.
        for(int i=0; i<showDoctor.size(); i++){
            Doctor d = showDoctor.get(i);
            System.out.println("Médico "+d.getCodMedico()+"  :  Pacientes asignados --> "+d.muestraListaPacientes());
        }
    }
    
    //Evaluo los resultados y los muestro.
    public void printResults() {
        System.out.println("\nMEJOR SOLUCIÓN\n"+minFitnessSoFar);
        //Muestro las asignaciones de pacientes de cada médico.
        muestraAsignaciones();
    }
    
}
