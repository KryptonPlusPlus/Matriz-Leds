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

import processing.serial.*;

// --- Objetos das Fontes utilizadas ---
PFont font;
PFont fontBold;
// --- ---
MatrizLeds matrizLed;
SwitchButton switchButton;
Serial serial;

void setup()
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

    try 
    {
        /*  Serial
         *      Porta Serial = /dev/ACM0
         *      BaudRate = 9600
         */
        serial = new Serial(this, "/dev/ttyACM0", 9600);
    }
    catch (Exception exc) 
    {
        print(exc);
        while(true)
        {
            print("\nFeche o progama e tente novamente!\n\n");
            System.exit(0);
        }
    }
    

    // Tamanho da janela
    size(854, 480); // 480p

    // Fonte
    font = createFont("Montserrat-Regular.ttf", 15);
    fontBold = createFont("Montserrat-Bold.ttf", 25);

    // Taxa de quadros
    frameRate(20); // 20fps

} // end setup

void draw()
{
    background(#232530);

    // Atualiza as informações dos botões
    matrizLed.updatePressedLed();

    // Painel de Leds
    stroke(#14171A);
    fill(#14171A);
    rect(25, 25, 425, 425, 10, 10, 10, 10);
    matrizLed.printMatrizLedsInDisplay();

    // Título do projeto
    stroke(#14171A);
    fill(#FFFFFF);
    textFont(fontBold);
    text("Painel de Leds", 475, 50);

    // SwitchButton
    switchButton.updateSwitch();
    switchButton.printSwitchButtonInDisplay();

    // SwitchButton Texto
    stroke(#14171A);
    fill(#FFFFFF);
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
void transferDataToArduino()
{
    // Vetor de tipo string com as informações dos leds
    String data[] = matrizLed.printMatrizLeds();

    // caracter para indicar quando começou a mensagem
    char charInitMatrizLeds       = '@';

    // caracter para indicar quando acabou a mensagem
    char charTerminatorMatrizLeds = '#';

    // mensagem 1
    serial.write(charInitMatrizLeds);
    for(int i = 0; i < 5; i++)
    {
        serial.write(data[i]);
    }
    serial.write(charTerminatorMatrizLeds);
    // caracter para indicar quando começou a mensagem
    char charInitRandom       = '&';

    // caracter para indicar quando acabou a mensagem
    char charTerminatorRandom = '$'; 
    
    // mensagem 2
    serial.write(charInitRandom);
    serial.write((switchButton.getOnOff() ? '1' : '0' ));
    serial.write(charTerminatorRandom);

} // end transferDataToArduino
