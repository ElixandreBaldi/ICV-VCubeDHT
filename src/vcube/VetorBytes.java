/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcube;

/**
 *
 * @author elixandre
 */
class VetorBytes {
    private byte[] arquivo;

    VetorBytes(byte[] arquivo){
        this.arquivo = new byte[arquivo.length];
        
        for(int i = 0; i < arquivo.length; i++){
            this.arquivo[i] = arquivo[i];
        }
    }
    
    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    public byte[] getArquivo() {
        return arquivo;
    }
}
