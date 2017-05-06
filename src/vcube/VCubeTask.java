/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcube;

import org.simgrid.msg.Msg;
import org.simgrid.msg.Task;

/**
 *
 * @author elixandre
 */
public class VCubeTask extends Task{
    private int[] timestemp;
    private int[] timestempStatus;    
    private int pretendente;

    private String emissor;
    private String conversarCom;  
    

    public VCubeTask(String name, double flopsAmount, double bytesAmount){
        super(name, flopsAmount,bytesAmount);
        timestemp = new int[8];
        timestempStatus = new int[8];
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
    public int getPretendente() {
        return pretendente;
    }
    
    public void setPretendente(int pret) {
        pretendente = pret;
    }
    
    public int[] getTimestempStatus(){
        return timestempStatus;
    }
    
    public void setTimestempStatus(int[] time){
        for(int i = 0; i<timestemp.length; i++)
            this.timestempStatus[i] = time[i];
    }
    
    public String getConversarCom() {
        return conversarCom;
    }

    public void setConversarCom(String conversar) {
        this.conversarCom = conversar;
    }
    
    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }
    
    public int[] getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(int[] timestemp) {
        for(int i = 0; i<timestemp.length; i++)
            this.timestemp[i] = timestemp[i];
    }        
}
