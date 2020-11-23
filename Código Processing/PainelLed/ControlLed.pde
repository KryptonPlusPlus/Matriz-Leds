// classe para o controle de um led

class ControlLed
{
    private int posX, posY;
    private boolean pressed;

    /**      Método contrutor
     *
     *   posX - posY --> Posição da circunferência
     */
    ControlLed(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    // get / set
    public void setPressed(boolean pressed)
    {
        this.pressed = pressed;
    }
    public boolean getPressed()
    {
        return pressed;
    }
    public int getPosX()
    {
        return posX;
    }

    public int getPosY()
    {
        return posY;
    }
}
