/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcube;

import static java.lang.Math.pow;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;

/**
 *
 * @author elixandre
 */
public class Node extends org.simgrid.msg.Process {
    private int id;   
    private int timestemp[];
    private int timestempStatus[];
    int status;
    int clusters;
    String solicitarPara;
    final int VAZIO = 0;
    final int OCUPADO = 1;
    boolean SOLICITANTE;
    VetorBytes[] arquivos; /// a partir daqui!!!
    
    public Node(Host hostname, String name, String[] args) throws HostNotFoundException {
        super(hostname,name,args);
        id = Integer.parseInt(args[0]);  
        arquivos = new VetorBytes[0];
        timestemp = new int[8];
        timestempStatus = new int[8];
        clusters = (int) (Math.log(8) / Math.log(2));
        for(int i=1;i<timestemp.length;i++){
            timestempStatus[i] = 1;
            if(id!=i){                
                timestemp[i] = 1;
            }
            else{                
                timestemp[i] = 0;
            }
        }
        
        timestempStatus[0]  = 0;
        timestemp[0]  = 0;
        
        if(id == 0){            
            status = OCUPADO;            
        }
        else if(id <= 7){
            status = VAZIO;
            SOLICITANTE = true;
            solicitarPara = "0";
        }        
        String var = "";
        for(int i = 0; i < timestempStatus.length; i++){                            
            var += timestempStatus[i]+" ";          
        }        
        
    }     
    public int lookup ( byte[] k ) {                
        BigInteger valor = new BigInteger(k);
        BigInteger dois = new BigInteger("2");
        
        BigInteger v = valor.divide(dois.pow(160-clusters)); 
        System.out.println("A chave pertence ao nodo "+v);
        int i = encontrarResponsavel(v.intValue());
        return i;
    }
    
    public int get (String chave) {        
        return lookup(gerarHash(chave, "SHA-1"));
    }
    private static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) s.append('0');
                s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public static byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(frase.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    public int[] CIS(int i, int s){             
        int k = (int) pow(2,s)/2; // QTD DE RETORNOS
        int[] retornoCis = new int[k];
        
        for(int x=0;x<k;x++){
            int p = (i^(int)pow(2,s-1));
            if(x==0)
                retornoCis[x]=p;
            else
                retornoCis[x]=(p^x);             
        }    
        
        return retornoCis;
    }
    public int[] CIS(int i){                     
        int[] retornoCis = new int[timestemp.length];        
        int s = clusters;
        int iRetorno = 0;
        while( iRetorno < timestemp.length-1){
            for(int x = 1; x <= s; x++){
                int[] vetorCis = CIS(i, x);
                for(int w = 0; w < vetorCis.length; w++){
                    
                    retornoCis[iRetorno] = vetorCis[w];
                    iRetorno++;
                }
            }
        }
        
        return retornoCis;
    }
    
    public int encontrarResponsavel(int v){
        int z = 0;
        int i = v;
        while( timestempStatus[i]%2 != 0 ){
            z++;
            i = v^z;
        }
        return i;    
    }
    
    public int encontrarMaisCheio(){
        int maisCheio = -1;
        int cont = 0, cont2 = 0;
        int qtdVazio = 0;
        for(int i = 0; i<timestempStatus.length; i++){
            if(timestempStatus[i]%2 != 0)
                qtdVazio++;
        }
        
        int[] responsaveis = new int[qtdVazio];
        int j = 0;
        
        for(int i = 0; i<timestempStatus.length; i++){
            if(timestempStatus[i]%2 != 0){
                responsaveis[j] = encontrarResponsavel(i);
                j++;
            }
        }        
        
        for(int i = 0; i < timestempStatus.length; i++){            
            if(timestempStatus[i]%2 == 0){                
                for(int k = 0; k<qtdVazio; k++){                    
                    if(responsaveis[k] == i){
                        cont++;
                    }
                }        
                if(cont > cont2){
                    cont2 = cont;
                    maisCheio = i;
                }
                cont = 0;
            }                        
        }
        return maisCheio;
    }
    
    public int encontrarMaisAdequado(){
        int maisCheio = encontrarMaisCheio();
        int cont = 0;
        int imaisAdequado;
        int maisAdequado = 0;
        int[] nodosPossiveis = new int[0];
        int nNodos = 0, iMaisCheio = -1;
        for(int i = 0; i < timestempStatus.length; i++){
            if(encontrarResponsavel(i) == maisCheio){
                int[] tmp = nodosPossiveis; // copia o array nodo atual para um temporário
                nNodos++; // incrementa o número de nodos
                nodosPossiveis = new int[nNodos]; // recria o array de nodos vazio, com o novo tamanho
                System.arraycopy(tmp, 0, nodosPossiveis, 0, tmp.length); // copia o array temporário para o novo
                nodosPossiveis[nNodos - 1] = i; // insere o novo nodo na listagem                                           
                
                if( i == maisCheio)
                    iMaisCheio = nNodos - 1;
            }            
        }
        
        if((iMaisCheio == nNodos - 1 || iMaisCheio == 0) && nNodos != 2){                                   
            return nodosPossiveis[nNodos/2];
        }
        if(nNodos - iMaisCheio > iMaisCheio)
            return nodosPossiveis[nNodos - 1];
        else
            return nodosPossiveis[0];                
    }
    public void conferirTimestemp(int[] timestempComparado){
        for(int i=0;i<timestemp.length;i++)
            if(timestemp[i] < timestempComparado[i] && i!=id)
                timestemp[i] = timestempComparado[i];  
    }
    public void conferirTimestempStatus(int[] timestempStatusComparado){        
        for(int i = 0; i < timestempStatus.length; i++)
            if(timestempStatusComparado[i] > timestempStatus[i] && i != id)
                timestempStatus[i] = timestempStatusComparado[i];        
    }
    public String setarNovoProcesso(int maisAdequado){
        VCubeTask NovoParticipante = new VCubeTask("Voce vai entrar",0,0);  
        Msg.info(id+" mandando "+maisAdequado+" entrar");
        SOLICITANTE = false;
        NovoParticipante.setEmissor(id+"");                                                                                                
        NovoParticipante.setTimestemp(timestemp);
        NovoParticipante.setTimestempStatus(timestempStatus);        
        if(timestempStatus[maisAdequado]%2 != 0)
            timestempStatus[maisAdequado]++;        
        NovoParticipante.dsend(""+maisAdequado);
        return "Pode dormir";
    }
    public void imprimirTimestemp(){
        String var = "";
        for(int i = 0; i < timestemp.length; i++){
            var += timestemp[i]+" ";          
        }
        Msg.info("Timestemp:      "+var);
    }
    public void imprimirTimestempStatus(){
        String var = "";
        for(int i = 0; i < timestempStatus.length; i++){
            var += timestempStatus[i]+" ";          
        }
        Msg.info("TimestempStatus:"+var);
    }
    public void solicitarEntrada(String solicitar){
        VCubeTask N = new VCubeTask("Quero Entrar",0,0);
        SOLICITANTE = false;
        N.setEmissor(""+id);
        N.dsend(solicitar);
    }
    public boolean existePossivelLocal(){
        for(int i = 0; i < timestemp.length; i++)
            if(timestemp[i]%2 == 0 && timestempStatus[i]%2 != 0 && i != id)
                return true;
                
        return false;
    }
    @Override
    public void main(String args[])throws MsgException{                
        String dorm ="";
        
        /*if(id == 0){
            //timestempStatus[1]++;
            timestempStatus[2]++;
            //timestempStatus[3]++;
            timestempStatus[4]++;
            //timestempStatus[5]++;
            //timestempStatus[6]++;
            //timestempStatus[7]++;
            System.out.println("responsavel pelo 0 é: "+encontrarResponsavel(0));
            System.out.println("responsavel pelo 1 é: "+encontrarResponsavel(1));
            System.out.println("responsavel pelo 2 é: "+encontrarResponsavel(2));
            System.out.println("responsavel pelo 3 é: "+encontrarResponsavel(3));
            System.out.println("responsavel pelo 4 é: "+encontrarResponsavel(4));
            System.out.println("responsavel pelo 5 é: "+encontrarResponsavel(5));
            System.out.println("responsavel pelo 6 é: "+encontrarResponsavel(6));
            System.out.println("responsavel pelo 7 é: "+encontrarResponsavel(7));
            
            System.out.println("Mais Adequado: "+encontrarMaisAdequado());
        
        }*/
        
        
        /*if(id == 0)
            for(int s = 1; s <= 3; s++){
                for(int i = 0; i < timestemp.length; i++){
                    int[] cis = CIS(i,s);
                    for(int j = 0; j < cis.length; j++){
                        System.out.printf(cis[j]+" ");
                    }
                    if(s == 1)
                        System.out.printf("        ");
                    else if(s == 2)
                        System.out.printf("      ");
                    else if(s == 3)
                        System.out.printf("  ");
                }
                System.out.println("");
            }
        */
        
        
        
        
                
        
        //for(int y=0;y<50;y++){    
        int contador = -1;
        
        do{ 
            contador++;               
            
            
            //imprimirTimestemp();
            //imprimirTimestempStatus();
            
            String EnviarPara="";            
            
            for(int s = 1; s <= clusters; s++){//Contactar nodos (VCube)                                                                 
                int[] VetorCis =  CIS(id,s);     
                for(int j = 0; j<VetorCis.length; j++){                
                    int [] VetorCJs = CIS(VetorCis[j], s);                        
                    for(int x=0; x<VetorCJs.length; x++){                                                
                        if(timestemp[VetorCJs[x]]%2 == 0){
                            if(VetorCJs[x]==id){//Ai vai enviar
                                EnviarPara += String.valueOf(VetorCis[j])+", ";
                                String envio = ""+VetorCis[j];
                                VCubeTask N = new VCubeTask("Ocupado?",0,0);                                    
                                N.setEmissor(args[0]); 
                                N.setTimestempStatus(timestempStatus);
                                N.dsend(envio);                                    
                                //Msg.info("Nodo "+id+" contatou "+VetorCis[j]);                                        
                            }
                            x=VetorCJs.length;                                                
                        }
                    }                                
                } 
            }                      
            
            if(SOLICITANTE){
                solicitarEntrada(solicitarPara);                
                Msg.info(id+" solicitando entrada ->> "+solicitarPara);                     
            }            
            
            waitFor(0.1);        
            
            VCubeTask R = null;            
            do{                            
                try{                                    
                    R = (VCubeTask) Task.receive(args[0],1);                        
                    
                    if(R.getName().equals("Voce vai entrar")){
                        status = OCUPADO;  
                        Msg.info(id+" Entreeei");
                        
                        conferirTimestemp(R.getTimestemp());
                        conferirTimestempStatus(R.getTimestempStatus());
                        if(timestemp[id]%2 != 0){
                            timestemp[id]++;                            
                        }
                        if(timestempStatus[id]%2 != 0){
                            timestempStatus[id]++;                            
                        }
                    }
                    else if(R.getName().equals("Ocupado?")){  
                        VCubeTask Resposta;
                        conferirTimestempStatus(R.getTimestempStatus());                         
                        if(status == OCUPADO)
                            Resposta = new VCubeTask("Estou Ocupado",0,0);                                                    
                        else
                            Resposta = new VCubeTask("Estou vazio",0,0);                        
                        Resposta.setTimestemp(timestemp);
                        Resposta.setTimestempStatus(timestempStatus);
                        Resposta.setEmissor(args[0]);                                                    
                        Resposta.dsend(R.getEmissor());                        
                    }else if(R.getName().equals("Estou Ocupado") || R.getName().equals("Estou vazio")){//CONFIRMAÇÃO DE RECEBIMENTO                          
                        EnviarPara = EnviarPara.replace(R.getEmissor()+", ", "");
                        conferirTimestemp(R.getTimestemp());                        
                        if(timestemp[Integer.parseInt(R.getEmissor())]%2 == 1)
                            timestemp[Integer.parseInt(R.getEmissor())]++; 
                        
                        if(R.getName().equals("Estou Ocupado")){//Confirmar TimestempStatus                            
                            conferirTimestempStatus(R.getTimestempStatus());                        
                            if(timestempStatus[Integer.parseInt(R.getEmissor())]%2 != 0)
                                timestempStatus[Integer.parseInt(R.getEmissor())]++;
                        }
                        else{ //Esta vazio                            
                            conferirTimestempStatus(R.getTimestempStatus());                        
                            if(timestempStatus[Integer.parseInt(R.getEmissor())]%2 == 0)
                                timestempStatus[Integer.parseInt(R.getEmissor())]++;
                        }                         
                    }                    
                    else if(R.getName().equals("Quero Entrar")){                          
                        VCubeTask Resposta = new VCubeTask("Resposta ao solicitante para entrar na rede",0,0);                        
                        if(existePossivelLocal()){
                            int maisAdequado = encontrarMaisAdequado();
                            int responsavel = encontrarResponsavel(maisAdequado);

                            if(responsavel == id)                            
                                Resposta.setConversarCom(setarNovoProcesso(maisAdequado));                            
                            else{//delegar inicialização de novo processo                            
                                Msg.info(id+ " mandando "+R.getEmissor()+" pedir para "+responsavel+" confirmacao do nodo "+maisAdequado);
                                Resposta.setConversarCom(responsavel+"");
                                Resposta.setPretendente(maisAdequado);                                
                            }        
                        }else{                            
                            Resposta.setConversarCom(id+"");
                        }
                        Resposta.setEmissor(args[0]);                                
                        Resposta.dsend(R.getEmissor());                        
                    }
                    else if(R.getName().equals("Confirmacao de Entrada")){
                        int pretendente = R.getPretendente();
                        VCubeTask Resposta = new VCubeTask("Resposta ao solicitante para entrar na rede",0,0);   
                        Msg.info(id+" analisando se o pretendente "+pretendente+" pode ser ocupado pelo "+R.getEmissor());                        
                        if(timestemp[pretendente] % 2 == 0 && timestempStatus[pretendente]%2 != 0)                            
                            Resposta.setConversarCom(setarNovoProcesso(pretendente));                        
                        else{
                            if(existePossivelLocal()){
                                int maisAdequado = encontrarMaisAdequado();
                                int responsavel = encontrarResponsavel(maisAdequado);
                                if(responsavel == id)                                                    
                                    Resposta.setConversarCom(setarNovoProcesso(maisAdequado));                                    
                                else{//delegar inicialização de novo processo                                    
                                    Msg.info(id+ " mandando "+R.getEmissor()+" pedir para "+responsavel+" confirmacao do nodo "+maisAdequado);                                    
                                    Resposta.setConversarCom(responsavel+"");
                                    Resposta.setPretendente(maisAdequado);
                                }    
                            }else{                                
                                Resposta.setConversarCom(id+"");
                            }
                        }
                        Resposta.setEmissor(args[0]);                                
                        Resposta.dsend(R.getEmissor());                        
                    }
                    else if(R.getName().equals("Resposta ao solicitante para entrar na rede")){ 
                        if(R.getConversarCom().equals("Pode dormir")){                                
                            //Msg.info("  Entrei, agora vou ir em bora");
                            SOLICITANTE = false;                                    
                        }                            
                        else if(!R.getConversarCom().equals(solicitarPara)){
                            solicitarPara = R.getConversarCom();                            
                            VCubeTask N = new VCubeTask("Confirmacao de Entrada",0,0);
                            N.setEmissor(args[0]);                               
                            N.setPretendente(R.getPretendente());
                            N.dsend(solicitarPara);
                            //Msg.info(id+"  confirmando entrada ->>"+solicitarPara);
                        }    
                        else{
                            SOLICITANTE = true;
                        }
                    }
                }catch(Exception e){                       
                    R = null;
                }
            }while(R != null);       
                                        
            String nodosFalhos[] = EnviarPara.split(",");                       
            for(int i = 0; i < nodosFalhos.length-1; i++){                                                                
                int valor = Integer.parseInt(nodosFalhos[i].trim());                
                if(timestemp[valor]%2 == 0 && valor!=id){
                   timestemp[valor]++; 
                   if(timestempStatus[valor] % 2 == 0 )
                      timestempStatus[valor]++;                   
                }
            }                    
        }while(true);       
    }
}