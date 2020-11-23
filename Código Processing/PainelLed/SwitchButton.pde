/*  Classe para facilitar o uso de Switch Button com uma
 * com uma facil implementação no código.
 */

class SwitchButton
{
    private int                posX; // posição X do switch
    private int                posY; // posição Y do switch
    private int              _width; // Largura do switch
    private int             _height; // Altura
    private int     _width_animated; // valor para fazer a animação
    private boolean          on_off; // ver se o botão está ligado 
    private boolean          locked;

    /**     Método construtor
     *
     * parâmetros:
     *      posX    - posY    --> Posição inicial      
     *      _height - _width  --> Largura e altura
     */
    public SwitchButton(int posX, int posY, int _width, int _height)
    {
        this.posX       = posX;
        this.posY       = posY;
        this._width     = _width;
        this._height    = _height;
        _width_animated = 0;
    }

    // Desenha no Display o Switch
    public void printSwitchButtonInDisplay()
    {
        stroke((on_off) ? #9e63f6 : #14171A);
        fill(  (on_off) ? #9e63f6 : #14171A);
        rect(posX, posY, _width, _height, 90, 90, 90, 90);
        
        stroke(#2f363d);
        fill(#2f363d);
        circle(((posX + (_height / 2)) + _width_animated / 2), (posY + (_height / 2)) + 1, _height);
    }

    // Atualiza as informações do switch
    public void updateSwitch()
    {
        if(overSwitch() && mousePressed && !locked)
        {
            locked = true;
            on_off = !on_off;
            _width_animated = ((on_off) ? (_width_animated + 10) :  (_width_animated - 10));
        }
        if((((posX + (_height / 2)) + _width_animated / 2) < (posX + _width - _height / 2)) && 
           (((posX + (_height / 2)) + _width_animated / 2) >          posX + (_height / 2)) && 
                                                                                    locked)
        {
            _width_animated = ((on_off) ? (_width_animated + 10) :  (_width_animated - 10));
        }
        else
        {
            locked = false;
        }
    }

    // Verifica se o mouse está dentro do switch
    public boolean overSwitch()
    {
        if(((sqrt(sq((posX + (_height / 2))         - mouseX) +                   // Verificação
                  sq((posY + (_height / 2))         - mouseY))  < _height / 2) || // dos
            (sqrt(sq((posX + (_width - (_height/2)))- mouseX) +                   // circulos    
                  sq((posY + (_height / 2))         - mouseY))  < _height / 2))   //
                                               ||
          (((mouseX > posX + _height/2) &&                                        // Verificação
            (mouseX < posX + _width))                                          && // do
           ((mouseY > posY)             &&                                        // rect
            (mouseY < posY + _height))))                                          //
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // get / set
    public boolean getOnOff()
    {
        return on_off;
    }
}
