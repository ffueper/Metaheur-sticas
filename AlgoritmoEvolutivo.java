package trabajofinalalgoritmica2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class AlgoritmoEvolutivo {
    private int numListDoctor; //Numero de médicos elegibles.
    private int numListPatient; //Numero de pacientes.
    
    private int genOfBestFound = 0;
    private double minFitnessSoFar = Double.MAX_VALUE; //Fitness de mejor solución
    private int[] bestSolutionSoFar = new int[numListPatient]; //Mejor Solución

    //Variables para tunear el algoritmo.
    private int populationSize = 400;      //Población de individuos.
    private int numGenerations = 900;      //Numero de generaciones.
    private double mutationRate = 0.01;    //Probabilidad de mutación del cromosoma.
    private double crossoverRate = 0.90;   //Probabilidad de cruce.
    private double clienteRatio = 0.9;     //Importancia de satisfacción de los clientes.
    private double empresaRatio = 0.1;     //Importancia de satisfacción de la empresa.
    private int numGensMutate = 2;         //Numero de genes que mutan

    private Random randomizer = new Random();
    
    private int[][] population;
    private double[] vectorFitness;
    
    private Patient[] listPatient; //Lista de pacientes
    private Doctor[] listDoctor;   //Lista de médicos

    public AlgoritmoEvolutivo() {
        System.out.println("_-_ ALGORITMO GENÉTICO _-_");
    }
    
    public double run(Doctor[] d, Patient[] p){
        //Introduzco los datos de prueba
        listDoctor = d;
        listPatient = p;
        numListDoctor = d.length;
        numListPatient = p.length;
        //System.out.println("Total de medicos disponibles: " + numListDoctor);
        //System.out.println("Total de pacientes para asignar: " + numListPatient);
        population = new int[populationSize][numListPatient]; //Matriz 100x20
        vectorFitness = new double[populationSize]; //Array donde cada posición es el fitnes de cada individuo(solución).
        //Genero la población inicial
        GenerateInitialPopulation();
        //Evaluo la población inicial
        printResults();
        //Evoluciono la población hasta el numero de generaciones deseado.
        evolve();
        //Evaluo la población final
        printResults();
        return minFitnessSoFar;
    }

    public void GenerateInitialPopulation() {
        //Inicializar población inicial y evaluar el fitness de cada individuo.
        for (int i = 0; i < populationSize; i++) {
            //Genero una solución inicial valida.
            boolean validate = false;
            while(!validate){
                for (int j = 0; j < numListPatient; j++) {
                    int selection = (int) (Math.random() * numListDoctor);  // Valores aleatorio 0, 9.
                    population[i][j] = selection;
                }
                //Guardo el fitness de cada individuo.
                vectorFitness[i] = fitness(population[i]);
                if(vectorFitness[i]!=Double.MAX_VALUE){
                    validate=true;
                }
            }
        }
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
            medicosSelecionados[v[i]] += 1;
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
    
    //Evoluciona la población de generación en generación hasta un criterio de parada dado.
    public void evolve() {
        for (int generation = 0; generation < numGenerations; generation++) {
            int[] selectedParents;
            int[][] childPool;
            //Muestro el mejor fitness hasta el momento
            //printBestFitness(generation);
            //Selecciona los padres por torneo.
            selectedParents = tournamentSelection();
            //Cruza los padres seleccionados para formar la nueva población.            
            childPool = uniformCrossover(selectedParents);

            //Mutación
            for (int i = 0; i < populationSize; i++) {
                //Si se cumple la condición del ratio de mutación.
                if (randomizer.nextDouble() < mutationRate) {
                    childPool[i] = nPointMutation(childPool[i]);
                }
            }

            //Por elitismo guardo las 2 soluciones más prometedora.
            childPool = elitism(childPool, generation);
            //Actualizo la población
            population = childPool;
            
            for (int i = 0; i < populationSize; i++) {
                //Guardo el fitness de cada individuo.
                vectorFitness[i] = fitness(population[i]);
            }
        }
    }
    
    //Metodo para aplicar el elitismo
    private int[][] elitism(int[][] childPool, int generation) {
        int best = 0;
        int almostBest = 1;
        int pos1, pos2;
        //Guarda los indices de los 2 mejores individuos de la población.
        for (int i = 1; i < populationSize; i++) {
            if (vectorFitness[i] < vectorFitness[best]) {
                almostBest = best;
                best = i;
            } else if (vectorFitness[i] < vectorFitness[almostBest]) {
                almostBest = i;
            }
        }

        //Actualiza la nueva mejor solución.
        if (vectorFitness[best] < minFitnessSoFar) {
            minFitnessSoFar = vectorFitness[best];
            bestSolutionSoFar = population[best];
            genOfBestFound = generation + 1;
        }

        //Selecciono 2 posiciones al azar que sean diferentes.
        do {
            pos1 = randomizer.nextInt(populationSize);
            pos2 = randomizer.nextInt(populationSize);
        } while (pos1 == pos2);
        //Sustituyo los 2 mejores individuos por los individuos seleccionados aleatoriamente.
        childPool[pos1] = population[best];
        childPool[pos2] = population[almostBest];
        //Actualizo los fitness
        vectorFitness[pos1] = vectorFitness[best];
        vectorFitness[pos2] = vectorFitness[almostBest];

        return childPool;
    }
    
    //Selecciona por torneo un padre por cada individuo de la población.
    private int[] tournamentSelection() {
        int[] selectedIndices = new int[populationSize];
        //Selecciona por torneo un padre por cada individuo de la población.
        for (int i = 0; i < populationSize; i++) {
            //Elige 2 individuos aleatoriamente.
            int ind1 = randomizer.nextInt(populationSize);
            int ind2 = randomizer.nextInt(populationSize);

            //Elige el cromosoma más fuerte
            int stronger;
            if (vectorFitness[ind1] < vectorFitness[ind2]) {
                stronger = 0;
            } else if (vectorFitness[ind1] > vectorFitness[ind2]) {
                stronger = 1;
            } else //Si son iguales elige uno de los 2 aleatoriamente.
            {
                stronger = randomizer.nextInt(2);
            }
            //Asigna el cromosoma más fuerte a la solucion
            if (stronger == 0) {
                    selectedIndices[i] = ind1;
                } else {
                    selectedIndices[i] = ind2;
            }
        }
        //Devuelve un array con un numero de padres igual al valor de PopulationSize
        return selectedIndices;
    }

    //Método de cruce
    private int[][] uniformCrossover(int[] selectedParents) {
        int[][] children = new int[populationSize][numListPatient];
        //Cruza los padres de 2 en 2 por orden en el que aparecen en el array.
        for (int i = 0; i < populationSize; i += 2) {
            //Si se cumple la condición del ratio de cruce, se procede al cruce de los 2 padres.
            if (randomizer.nextDouble() < crossoverRate) {
                //El nuevo individuo tendrá en cada posición del cromosoma un gen de alguno de los 2 padres. El padre que aporta cada gen se elige al hazar.
                for (int j = 0; j < numListPatient; j++) {
                    //Si sale true se escoge el gen de la posición j del padre 1 para el hijo 1 y del padre 2 para el hijo 2
                    if (randomizer.nextBoolean()) {
                        children[i][j] = population[selectedParents[i]][j];
                        //Si el numero de la población es PAR
                        if (i < populationSize - 1) {
                            children[i + 1][j] = population[selectedParents[i + 1]][j];
                        } else {
                            //Si el numero de la población es IMPAR
                            children[0][j] = population[selectedParents[0]][j];
                        }
                    //Si NO sale true se escoge el gen de la posición j del padre 2 para el hijo 1 y del padre 1 para el hijo 2
                    } else {
                        if (i < populationSize - 1) { //Si el numero de la población es PAR
                            children[i][j] = population[selectedParents[i + 1]][j];
                            children[i + 1][j] = population[selectedParents[i]][j];
                        } else { //Si el numero de la población es IMPAR
                            children[i][j] = population[selectedParents[0]][j];
                            children[0][j] = population[selectedParents[i]][j];
                        }
                    }
                }//Fin for
            } else { //Si no se cumple el ratio de cruce, el hijo1 será una copia del padre1 y el hijo2 será una copia del padre2
                for (int j = 0; j < numListPatient; j++) {
                    children[i][j] = population[selectedParents[i]][j];
                    if (i < populationSize - 1) {//Si el numero de la población es PAR
                        children[i + 1][j] = population[selectedParents[i + 1]][j];
                    } else {//Si el numero de la población es IMPAR
                        children[0][j] = population[selectedParents[0]][j];
                    }
                }
            }
        }
        return children;
    }

    //Método mutación.
    private int[] nPointMutation(int[] c) {
        int posMutation;
        int aux;
        //Muta tantos genes como el valor numGensMutate.
        for (int i = 0; i < numGensMutate; i++) {
            //elige un gen del cromosoma
            posMutation = (int) (Math.random()*c.length);
            //muta el gen. Si el valor del gen es 0 se suma 1. Si el valor del gen no es 0 se resta 1.
            aux = (int) (Math.random()*2);
            if(c[posMutation]==0){
                c[posMutation]++;
            }else{
                c[posMutation]--;
            }
        }
        return c;
    }

    

    //Evaluo los resultados y los muestro.
    public void printResults() {
        int best = 0;
        int almostBest = 1;
        int pos1, pos2;
        //Guarda los indices de los 2 mejores individuos de la población.
        for (int i = 1; i < populationSize; i++) {
            if (vectorFitness[i] < vectorFitness[best]) {
                almostBest = best;
                best = i;
            } else if (vectorFitness[i] < vectorFitness[almostBest]) {
                almostBest = i;
            }
        }

        //Actualiza la nueva mejor solución.
        if (vectorFitness[best] < minFitnessSoFar) {
            minFitnessSoFar = vectorFitness[best];
            bestSolutionSoFar = population[best];
        }
        System.out.println("\nMEJOR SOLUCIÓN ENCONTRADA en la generación "+genOfBestFound+"\n"+minFitnessSoFar);
        //muestraPoblacion();
        //System.out.println("");
        muestraAsignaciones();
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
    
    //Muestra la pablación de individuos
    public void muestraPoblacion(){
        System.out.println("\nMUESTRO POBLACIÓN ACTUAL:");
        for(int i=0; i<populationSize; i++){
            for(int j=0; j<numListPatient; j++){
                System.out.print(population[i][j]+", ");
            }
            System.out.println("");
        }
    }
    
    //Muestra la generación pasada por parametro y el mejor fitness actual.
    public void printBestFitness(int generation){
        System.out.println("\nGeneración "+generation+"  :  Mejor fitness "+minFitnessSoFar);
    }
}
