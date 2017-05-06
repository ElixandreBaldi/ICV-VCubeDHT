/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcube;

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
    public static void main(String[] args) throws HostNotFoundException {
        // TODO code application logic here
        Msg.init(args);      
        Msg.createEnvironment(args[0]);         
        Msg.deployApplication(args[1]);        
        Msg.run();        
    }       
}
