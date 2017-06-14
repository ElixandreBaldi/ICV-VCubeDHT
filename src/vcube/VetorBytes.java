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
    private byte[] hash;
    private String arquivo;
    private int dono;

    public int getDono() {
        return dono;
    }

    public void setDono(int dono) {
        this.dono = dono;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    VetorBytes(byte[] hash, String arquivo, int id){
        dono = id;
        this.hash = new byte[hash.length];
        this.arquivo = arquivo;
        for(int i = 0; i < hash.length; i++){
            this.hash[i] = hash[i];
        }
    }
    
    public void setHash(byte[] arquivo) {
        this.hash = arquivo;
    }

    public byte[] getHash() {
        return hash;
    }
}