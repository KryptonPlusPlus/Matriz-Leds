import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PainelLed extends PApplet {

/** PainelLed.pde
 * 
 *    Software para comunicação do usuario com o arduino
 *
 *   Seleciona qual led deve ser acionado ou desacionado na matriz
 *  ou seleciona a opção de ficar ligando ou desligando os leds 
 *  aleatoriamente, fazendo essa comunicação via Serial.
 *  
 *  @version 1.0.0
 */

// Biblioteca para fazer a comunicação serial



// --- Objetos das Fontes utilizadas ---
PFont font;
PFont fontBold;
// --- ---
MatrizLeds matrizLed;
SwitchButton switchButton;
Serial serial;

public void setup()
{
    /*  Matriz de Leds 
     *      Posição x = 75 - y = 75
     *      Tamanho 5x5
     *      Largura = 400 - Altura = 400
     *      Raio = 50
     */
    matrizLed = new MatrizLeds(75, 75, 5, 5, 400, 400, 50);

    /*  Switch Button
     *      Posição x = 475 - y = 425
     *      Largura = 50 - Altura = 25
     */
    switchButton = new SwitchButton(475, 425, 50, 25);

    /*  Serial
     *      Porta Serial = /dev/ACM0
     *      BaudRate = 9600
     */
    try 
    {
        serial = new Serial(this, "/dev/ACM0", 9600);
    }
    catch (Exception e) 
    {
        print(e);
        while(true)
        {
            print("\nFeche o progama e tente novamente!");
            delay(1000);
        }
    }
    

    // Tamanho da janela
     // 480p

    // Fonte
    font = createFont("Montserrat-Regular.ttf", 15);
    fontBold = createFont("Montserrat-Bold.ttf", 25);

    // Taxa de quadros
    frameRate(20); // 20fps

} // end setup

public void draw()
{
    background(0xff232530);

    // Atualiza as informações dos botões
    matrizLed.updatePressedLed();

    // Painel de Leds
    stroke(0xff14171A);
    fill(0xff14171A);
    rect(25, 25, 425, 425, 10, 10, 10, 10);
    matrizLed.printMatrizLedsInDisplay();

    // Título do projeto
    stroke(0xff14171A);
    fill(0xffFFFFFF);
    textFont(fontBold);
    text("Painel de Leds", 475, 50);

    // SwitchButton
    switchButton.updateSwitch();
    switchButton.printSwitchButtonInDisplay();

    // SwitchButton Texto
    stroke(0xff14171A);
    fill(0xffFFFFFF);
    textFont(font);
    text("Ativar o modo aleatório", (width - 200), 445);
   
    // Ativa ou desativa o cursor de mão ou de seta
    cursor((matrizLed.overAllCircle() || switchButton.overSwitch()) ? HAND : ARROW);


    /*    Manda os dados para o arduino a cada 1s (nessa configuração), para evitar
     * sobrecarregar os arduino
     */
    if(frameCount % 20 == 0)
    {
        /*  Cria uma tarefa separada para a comunicação com o arduino, para
         * não interromper o tempo de execução do código principal
         */
        thread("transferDataToArduino");
    }

} // end draw

// Função para fazer a comunicação com o arduino
public void transferDataToArduino()
{
    // Vetor de tipo string com as informações dos leds
    String data[] = matrizLed.printMatrizLeds();

    // caracter para indicar quando começou a mensagem
    char charInit       = '@';

    // caracter para indicar quando acabou a mensagem
    char charTerminator = '#';

    print(charInit);

    for(int i = 0; i < 5; i++)
    {
        serial.write(data[i]);
    }

    // getOnOff()
    // Pegar se o switch está ativo
    // Possível implementação futura

    print(charTerminator);

} // end transferDataToArduino
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
                matriz_leds[i] += (((array_leds[i][j].getPressed()) ? 1 : 0) + ((j != (width_leds - 1)) ? "" : "\n"));
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
                stroke(0xff232530);
                fill((array_leds[i][j].getPressed()) ? 0xff9e63f6 : 0xffFFFFFF);
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
        stroke((on_off) ? 0xff9e63f6 : 0xff14171A);
        fill(  (on_off) ? 0xff9e63f6 : 0xff14171A);
        rect(posX, posY, _width, _height, 90, 90, 90, 90);
        
        stroke(0xff2f363d);
        fill(0xff2f363d);
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
  public void settings() {  size(854, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PainelLed" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
