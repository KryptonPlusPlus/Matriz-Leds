/* MatrizLeds.ino 
 * 
 *  O projeto tem a intenção de controlar um painel de leds 5x5,
 * usando um software criado no processing, ou pode ativar uma
 * opção que faz os leds ficarem acendendo e delisgando de modo
 * aleatório(função controlada no software, usando um switch).
 * 
 * @author Arthur S. - Andre V. - Caio K. - Gabriel I. 
 */

// Pinos dos leds das linhas
const int pins_leds_line[5]  {8,  7,  6,  5,  4};
// Pinos dos leds das colunas
const int pins_leds_column[5]{13, 12, 11, 10, 9};

// Matriz com os valores dos leds
bool value[5][5];
// Ativa ou desativa o modo aleatório
bool random_mode;

void setup()
{
    // Define os pinos como saída
    for (int i = 0; i < 5; i++)
    {
        pinMode(pins_leds_line[i], 1);//define os pinos da linha
        pinMode(pins_leds_column[i], 1);//define os pinos da coluna 
    }

    // Conecta na Serial
    Serial.begin(9600);
    while(!Serial){;}

} // end setup

void loop()
{

    // Verifica se tem informação na serial
    if(Serial.available() > 0)
    {
        if(!random_mode)
        {
            // Faz a leitura dos valores da serial e atribui eles aos vetores de linha e coluna 
            assignsValuesSerialToVetor();
        }
        
        // Faz a leitura dos valores da serial para ativar ou desativar o modo aleatorio
        activateRandomMode();

        // Se verdadeiro mdo aleatório ativado
        if(random_mode)
        {
            modeRadom();
        }
    
    } // end if Serial.available
    

    // Liga e desliga os pinos do arduino
    writeInPin();
} // end loop

// --------------------------------------------------------------------------------------

// Faz a leitura dos valores da serial e atribui eles aos vetores de linha e coluna 
void assignsValuesSerialToVetor()
{
    // Faz a leitura de serial
    char read_char = timeReadSerial();

    // Verifica se a leitura é igual a '@' (caracter inicializador, definido no processing)
    if(read_char == '@')
    {
        // limpa a matriz dos leds
        clearMatrizLeds();

        int line   = 0; // Valor para as linha
        int column = 0; // Valor para as colunas

        read_char = timeReadSerial();

        // Enquanto difente de '#' (Caracter final, definido no processing
        while(read_char != '#')
        {
            // Verifica se column diferente de 5 para evitar erros durante a execução
            // pois é uma matriz 5x5
            if(column != 5)
            {
                // Verifica se o caracter lido é igual a 1
                if(read_char == '1')
                {
                    value[line][column] = 1;
                }
            }

            // Navega entre as colunas da matriz
            column++;
            // Verifica se chegou no final das colunas da matriz
            if(column == 5)
            {
                // Incrementa as linhas
                line++;
                // Reseta as colunas
                column = 0;
            }
            // Verifica se execedeu os valores da matriz se verdadeiro sai do loop
            if(line == 5 && column == 1) { break; }

            // Faz a leitura da Serial
            read_char = timeReadSerial();
        }
    }
}
void activateRandomMode()
{
    // Faz a leitura de serial
    char read_char = timeReadSerial();

    // Verifica se a leitura é igual a '&' (caracter inicializador, definido no processing)
    if(read_char == '&')
    {
        read_char = timeReadSerial();

        // Enquanto difente de '$' (Caracter final, definido no processing
        while(read_char != '$')
        {
            // Verifica se o modo aleatório foi ativado
            if(read_char == '1')
            {
                random_mode = 1;
            }
            
            break;
        }
    }
}

void modeRadom()
{
    int random_i = (int)random(5);
    int radom_j  = (int)random(5);

    value[random_i][radom_j] = !value[random_i][radom_j];
}

// Liga e desliga os pinos do arduino
void writeInPin()
{
    // Percorre a matriz dos valores
    for(int i = 0; i < 5; i++)
    {
        for(int j = 0; j < 5; j++)
        {
            // Liga ou desliga as colunas e as linhas
            digitalWrite(pins_leds_column[i], ((value[i][j] == 1) ? 0 : 1));
            digitalWrite(pins_leds_line[j], ((value[i][j] == 1) ? 1 : 0));
        }
        // Delay parar dar a impressão de que os leds estão ligados
        delayMicroseconds(40);
        // Limpa as portas
        for(int j = 0; j < 5; j++)
        {
            digitalWrite(pins_leds_column[j], 1);
            digitalWrite(pins_leds_line[j], 0);
        }
    }
}
/*   Função para ler um caracter da Serial, juntamente com a espera para a 
 * possibilidade do proximo caracter, de 100ms
 */
char timeReadSerial()
{
    unsigned long __startTime = millis();
    do {
        char tempChar = Serial.read();

        if(tempChar > 0) return tempChar;
    } while((millis() - __startTime) < 100);

    return 0;
}

// Função para limpar a matriz dos leds
void clearMatrizLeds()
{
    for(int i = 0; i < 5; i++)
    {
        for(int j = 0; j < 5; j++)
        {
            value[i][j] = 0;
        }
    }
}
