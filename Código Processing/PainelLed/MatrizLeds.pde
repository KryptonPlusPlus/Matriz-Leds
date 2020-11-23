// height = y = i = Altura
// width  = x = j = Largura

/*   Classe para fazer o controle de uma matriz de leds, em cojunto com 
 *  a classe "ControlLed"
 */
class MatrizLeds
{
    private ControlLed array_leds[][];  // Matriz com objetos do tipo PainelLed
    private int        height_leds;     // Quantidade de leds na altura
    private int        width_leds;      // Quantidade de leds na largura
    private int        _height;         // Altura da matriz
    private int        _width;          // Largura da matriz
    private int        dim_circle;      // diâmetro do circulo do led
    private boolean    locked;          // "trava" de verificação

    /**     Método construtor
     *
     * parâmetros:
     *      posX    - posY    --> Posição inicial da matriz
     *      x       - y       --> Quantidade de leds no eixo x e y
     *      _height - _width  --> Largura e altura da matriz
     *      rad               --> diâmetro do led
     */
    MatrizLeds(int posX, int posY, int x, int y, int _height, int _width, int rad)
    {
        array_leds   = new ControlLed[y][x];
        height_leds  = y;
        width_leds   = x;
        this._height = _height;
        this._width  = _width;
        dim_circle   = rad;

        for (int i = 0; i < height_leds; i++)
        {
            for (int j = 0; j < width_leds; j++)
            {
                // Cria a matriz de ControlLed e posiciona os leds igualmente dentro da matriz
                ControlLed leds = new ControlLed((((_width / x) * j) + posX), 
                                                 (((_height / y) * i) + posY));
                // Atribui o valor coletados em leds a matriz leds
                array_leds[i][j] = leds;
            }
        }
    }  

    // Verifica se os circulos estão sendo pressionados 
    public void updatePressedLed()
    {
        for(int i = 0; i < height_leds; i++)
        {
            for(int j = 0; j < width_leds; j++)
            {
                if(overCircle(i, j) && mousePressed && !locked)
                {
                    // "trava" para esperar o usuário soltar o botão do mouse evitando miss click 
                    locked = true;

                    // Verifica se o botão já estava pressionados, se sim ele solta, e se não ele preciona
                    array_leds[i][j].setPressed((array_leds[i][j].getPressed()) ? (false) : (true));
                }
                if(!mousePressed)
                {
                    // Desativa a "trava"
                    locked = false;
                }
            }
        }
    }

    // Verifica se o mouse está dentro do circulo
    public boolean overCircle(int i, int j)
    {
        if((sqrt(sq(array_leds[i][j].getPosX() - mouseX) +
                 sq(array_leds[i][j].getPosY() - mouseY)) < dim_circle / 2))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
        
    public boolean overAllCircle()
    {
        for (int i = 0; i < height_leds; i++) 
        {
            for (int j = 0; j < width_leds; j++) 
            {
                if((sqrt(sq(array_leds[i][j].getPosX() - mouseX) +
                         sq(array_leds[i][j].getPosY() - mouseY)) < dim_circle / 2))
                {
                    return true;
                }
            }
        }
        return false;
    }
   
    // Printa os valores da matriz no terminal
    public String[] printMatrizLeds()
    {
        String matriz_leds[] = new String[height_leds];

        for(int i = 0; i < height_leds; i++)
        {
            matriz_leds[i] = "";
            for(int j = 0; j < width_leds; j++)
            {
                matriz_leds[i] += (((array_leds[i][j].getPressed()) ? '1' : '0'));
            }
        }

        return matriz_leds;
    }

    // Printa a matris na tela 
    public void printMatrizLedsInDisplay()
    {
        for(int i = 0; i < height_leds; i++) 
        {
            for (int j = 0; j < width_leds; j++)
            {
                stroke(#232530);
                fill((array_leds[i][j].getPressed()) ? #9e63f6 : #FFFFFF);
                circle(array_leds[i][j].getPosX(), array_leds[i][j].getPosY(), dim_circle);
            }
        }
    }

    // get/set
    // AllMatrizLeds
    public ControlLed[][] getAllMatrizLeds()
    {
        return array_leds;
    }
    public void setAllMatrizLeds(ControlLed value[][])
    {
        this.array_leds = value;
    }

    // Leds Pressionados
    public boolean getPressedLed(int x, int y)
    {
        return array_leds[y][x].getPressed();
    }  

    public void setPressedLed(int x, int y, boolean value)
    {
        array_leds[y][x].setPressed(value);
    }

    // Quantidade de leds na Matriz
    public int getMatrizLedsHeight()
    {
        return height_leds;
    }
    
    public int getMatrizLedsWidth()
    {
        return width_leds;
    }

    // Tamanho da matriz
    public int getMatrizHeight()
    {
        return _height;
    }
    
    public int getMatrizWidth()
    {
        return _width;
    }

    // Diâmetro do circulo do led
    public int getDimCircle()
    {
        return dim_circle;
    }
}
