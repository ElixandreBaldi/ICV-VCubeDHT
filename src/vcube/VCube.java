/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcube;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.Msg;


/**
 *
 * @author elixandre
 */
public class VCube {

    /**
     * @param args the command line arguments
     */
    public static final int QTDNODOS = 16;
    public static void criarPlatform() throws IOException{        
        FileWriter fileW = new FileWriter ("platform.xml");//arquivo para escrita
        BufferedWriter buffW = new BufferedWriter (fileW);
        
        String texto = "";
        texto+="<?xml version='1.0'?>\n" +
                "<!DOCTYPE platform SYSTEM \"http://simgrid.gforge.inria.fr/simgrid/simgrid.dtd\">\n" +
                    "<platform version=\"4\">\n" +
                    "	<AS id=\"AS_2\" routing=\"Dijkstra\">\n";
        for(int i = 0; i < QTDNODOS; i++) {
            texto+="		<host id=\""+i+"\" speed=\"137.333Mf\" />\n";
        }
        texto+="		<link id=\"10\" bandwidth=\"3.430125MBps\" latency=\"536.941us\"/>		\n" +
                "		<router id=\"central_router\"/>\n";
        
        for(int i = 0; i < QTDNODOS; i++){
            texto+="		<route src=\"central_router\" dst=\""+i+"\" symmetrical=\"NO\"><link_ctn id=\"10\"/></route>\n" +
                    "		<route src=\""+i+"\" dst=\"central_router\" symmetrical=\"NO\"><link_ctn id=\"10\"/></route>\n";
        }
        texto+="	</AS>\n" +
                "</platform>";
                    
        buffW.write(texto);
        buffW.flush();
    }
        
    public static void criarDeployment() throws IOException{        
        FileWriter fileW = new FileWriter ("deployment.xml");//arquivo para escrita
        BufferedWriter buffW = new BufferedWriter (fileW);
        
        String texto = "";
        texto+="<?xml version='1.0'?>\n" +
                "<!DOCTYPE platform SYSTEM \"http://simgrid.gforge.inria.fr/simgrid/simgrid.dtd\">\n" +
                "<platform version=\"4\">\n";
        for(int i = 0; i < QTDNODOS; i++) {
            texto+="	<process host=\""+i+"\" start_time=\"0\" function=\"vcube.Node\">\n" +
                    "		<argument value=\""+i+"\"/>		       \n";
            if(i == 47 || i == 114){
                texto+="		<argument value=\"texto para simulaçao\"/>\n";
            }
                    texto+="	</process>\n";
        }        
        texto+="</platform>";
                    
        buffW.write(texto);
        buffW.flush();
    }
    public static void main(String[] args) throws HostNotFoundException, IOException {
        // TODO code application logic here
       criarDeployment();
       criarPlatform();
        Msg.init(args);      
        Msg.createEnvironment(args[0]);         
        Msg.deployApplication(args[1]);        
        Msg.run();        
        
        
        
    }       
}
