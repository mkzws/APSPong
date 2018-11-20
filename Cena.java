package apspong;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Font;

public class Cena implements GLEventListener, KeyListener {

    private boolean menu = true;
    private float x = 0;
    private float bx = 0;
    private float by = 0;
    private float bxSpeed = 3;
    private float bySpeed = 3;
    private float bxAngle = (float) Math.random();
    private float angulo = 0;
    private int pontos = 0;
    private int vidas = 5;
    private int fase = 1;
    private GL2 gl;
    private GLU glu;
    private GLUT glut;
    private int tonalizacao = GL2.GL_SMOOTH;
    private boolean liga = false;
    private int modo = GL2.GL_FILL;

    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        GL2 gl = drawable.getGL().getGL2();
        //habilita o buffer de profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);
        if (bxAngle <= 0.5) {
            bxAngle = bxAngle * -1;
        }
        bySpeed = (int) Math.random() * 100 % 2;
        if (bySpeed == 1) {
            bySpeed = 2;
        } else {
            bySpeed = -2;
        }

        bxSpeed = bxSpeed * bxAngle;
        System.out.println(bxAngle);
        System.out.println(bxSpeed);
        System.out.println(bySpeed);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        //obtem o contexto Opengl
        gl = drawable.getGL().getGL2();
        glut = new GLUT(); //objeto da biblioteca glut

        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 1);
        //limpa a janela com a cor especificada
        //limpa o buffer de profundidade
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); //lê a matriz identidade

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, modo);
        /*
            desenho da cena        
        *
         */
        // criar a cena aqui....
        gl.glRotatef(angulo, 0.0f, 1.0f, 1.0f);

        if (liga) {
            iluminacaoEspecular();
            ligaLuz();
        }
        if (menu) {
            telaInicial();
        } else {
            gl.glPushMatrix();
            gl.glTranslatef(x, -90, 0);
            gl.glColor3f(1, 1, 1);
            barra();
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glTranslatef(bx, by, 0);
            move();
            gl.glColor3f(1, 1, 1);
            bolinha();
            gl.glPopMatrix();

            vidas();
            score();
            if (liga) {
                desligaluz();
            }
            if (pontos >= 5) {
                fase = 2;
                fase2();
            }
        }
        gl.glFlush();
    }

    private void telaInicial() {
        TextRenderer renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
        renderer.beginRendering(800, 800);
        // optionally set the color
        renderer.draw("Pressione Espaço para iniciar o jogo ", 100, 500);
        renderer.endRendering();
    }

    private void move() {
        bx = bx + bxSpeed;
        by = by + bySpeed;

        //colisão com os cantos da tela
        if (bx >= 100) {
            bxSpeed = bxSpeed * -1;
        }
        if (by >= 100) {
            bySpeed = bySpeed * -1;
        }
        if (bx <= -100) {
            bxSpeed = bxSpeed * -1;
        }
        //bolinha passou da raquete
        if (by <= -100) {
//            bySpeed = bySpeed * -1;
            System.out.println("eroooooou!");
            vidas--;
            bx = 0;
            by = 50;
//            bxAngle = (int) (Math.random() * 10);
//            System.out.println("bxAngle: " + bxAngle);
//            if (bxAngle <= 5) {
//                bxAngle = bxAngle * -1;
//            }

//            bxSpeed = bxSpeed * bxAngle;
            System.out.println("bxAngle: " + bxAngle);
            System.out.println("bxSpeed: " + bxSpeed);

            bySpeed = (int) (Math.random() * 100) % 2;
            System.out.println("bySpeed: " + bySpeed);
            if (bySpeed == 1) {
                bySpeed = 2;
            } else {
                bySpeed = -2;
            }
            System.out.println("bySpeed: " + bySpeed);
        }

        /**
         * colisão com a raquete, como a raquete está sempre em movimento, não é
         * possível saber onde ela vai estar exatamente, então utilizamos a
         * variável X para saber onde está o meio dela, logo, X + 15 representa
         * o canto direito da raquete, X - 15 representa o canto esquerdo da
         * raquete e -90 é a altura que ela esta *
         */
        if (bx <= x + 15 && bx >= x - 15 && by <= -90) {
            //se a bolinha colidir com o lado direito da raquete, ela volta pela direita
            if (bx <= x + 15 && bx >= x + 5 && by <= -90) {
                if (bxSpeed < 0) {
                    bxSpeed = bxSpeed * -1 + .1f;
                }
                if (bxSpeed > 0) {
                    bxSpeed = bxSpeed + .1f;
                }

                System.out.println("bxAngle: " + bxAngle);
            }
            //se a bolinha colidir com o lado esquerdo da raquete, ela volta pela esquerda
            if (bx <= x - 5 && bx >= x - 15 && by <= -90) {
                if (bxSpeed > 0) {
                    bxSpeed = bxSpeed * -1 - .1f;
                }
                if (bxSpeed < 0) {
                    bxSpeed = bxSpeed - .1f;
                }
                System.out.println("bxAngle: " + bxAngle);
            }
            //se ela colidiu no meio então ele só rebate do jeito que ela veio sem trocar o lado
            bySpeed = bySpeed * -1 + 0.1f;
            //se a bolinha colidiu com a raquete e você ainda possuir vidas, então o contador de pontos é incrementado
            if (vidas > 0) {
                pontos++;
            }

            System.out.println("bxSpeed: " + bxSpeed);
            System.out.println("acertou");
            System.out.println("bySpeed" + bySpeed);
        }
    }

    private void vidas() {
        for (int i = 0; i < vidas; i++) {
            gl.glPushMatrix();
            gl.glTranslatef(i * 20 - 40, 80, 0);
            estrela();
            gl.glPopMatrix();
        }
    }

    private void fase2() {

        gl.glPushMatrix();
        gl.glScalef(3, 1, 1);
        gl.glColor3f(.2f, .2f, .2f);
        barra();
        gl.glPopMatrix();

        if (by <= 1 && by >= -10 && bx <= 40 && bx >= 39.5f) {
            bxSpeed = bxSpeed * -1;
        }
        if (by <= 1 && by >= -10 && bx >= -40 && bx <= -39.5f) {
            bxSpeed = bxSpeed * -1;
        }
        if (bx >= - 40 && bx <= 40 && by <= 1 && by >= -10) {
            bySpeed = bySpeed * -1;
            System.out.println("bySpeed: " + bySpeed);
            System.out.println("bxSpeed: " + bxSpeed);
        }

    }

    private void score() {

        TextRenderer renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
        renderer.beginRendering(800, 800);
        // optionally set the color
        if (vidas > 0) {
            renderer.draw("Pontos: " + pontos, 0, 710);
            renderer.draw("Fase: " + fase, 650, 710);
        } else {
            renderer.draw("Perdeeu! \n Pontos: " + pontos + " Fase: " + fase, 250, 500);
        }
        // ... more draw commands, color changes, etc.
        renderer.endRendering();
    }

    private void estrela() {

        gl.glScalef(10f, 16f, 0);

        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f(1f, 0.56f, 0.64f);
        gl.glVertex2f(-0.2f, 0.3f);
        gl.glVertex2f(0, 0.7f);
        gl.glVertex2f(0.2f, 0.3f);
        gl.glVertex2f(0.7f, 0.3f);
        gl.glVertex2f(.3f, -0.f);
        gl.glVertex2f(.6f, -0.5f);
        gl.glVertex2f(.0f, -0.2f);
        gl.glVertex2f(-0.6f, -0.5f);
        gl.glVertex2f(-0.3f, -0.f);
        gl.glVertex2f(-0.7f, .3f);
        gl.glEnd();
    }

    private void barra() {

        gl.glScalef(20f, 26f, 0);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex2f(-0.7f, -0.3f);
        gl.glVertex2f(-0.7f, 0.0f);
        gl.glVertex2f(0.7f, 0.0f);
        gl.glVertex2f(0.7f, -0.3f);

        gl.glEnd();
    }

    private void bolinha() {
        gl.glScalef(0.5f, 0.5f, 0);
        double limite = 2 * Math.PI;
        double i, centroX, centroY, rX, rY;

        centroX = 0;
        centroY = 0;
        //Valores diferentes geram elipses
        rX = 4f;
        rY = 4f;

        gl.glBegin(GL2.GL_POLYGON);
        for (i = 0; i < limite; i += 0.01) {
            //centroX + raioX,  centroY e raioY		      
            gl.glVertex2d(centroX + rX * Math.cos(i),
                    centroY + rY * Math.sin(i));
        }

        gl.glEnd();

    }

    public void iluminacaoEspecular() {
        float luzAmbiente[] = {0f, 0.0f, 0f, 0f}; //cor
        float luzEspecular[] = {1.0f, 0.0f, 1.0f, 1.0f}; //cor
        float posicaoLuz[] = {35.0f, 35.0f, 35.0f, 0.0f}; //pontual

        //intensidade da reflexao do material        
        int especMaterial = 128;
        //define a concentracao do brilho
        gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, especMaterial);

        //define a reflectÃ¢ncia do material
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, luzEspecular, 0);

        //define os parÃ¢metros de luz de nÃºmero 0 (zero)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, luzEspecular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);
    }

    public void ligaLuz() {
        // habilita a definiÃ§Ã£o da cor do material a partir da cor corrente
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // habilita o uso da iluminaÃ§Ã£o na cena
        gl.glEnable(GL2.GL_LIGHTING);
        // habilita a luz de nÃºmero 0
        gl.glEnable(GL2.GL_LIGHT0);
        //Especifica o Modelo de tonalizacao a ser utilizado 
        //GL_FLAT -> modelo de tonalizacao flat 
        //GL_SMOOTH -> modelo de tonalizaÃ§Ã£o GOURAUD (default)        
        gl.glShadeModel(tonalizacao);
    }

    public void desligaluz() {
        //desabilita o ponto de luz
        gl.glDisable(GL2.GL_LIGHT0);
        //desliga a iluminacao
        gl.glDisable(GL2.GL_LIGHTING);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //obtem o contexto grafico Opengl
        gl = drawable.getGL().getGL2();
        //ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); //lê a matriz identidade
        //projeção ortogonal (xMin, xMax, yMin, yMax, zMin, zMax)
        gl.glOrtho(-100, 100, -100, 100, -100, 100);
        //ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        System.out.println("Reshape: " + width + ", " + height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                if (menu) {
                    pontos = 0;
                    fase = 1;
                    menu = false;
                } else {
                    if(vidas <= 0){
                        pontos = 0;
                        fase = 1;
                        vidas = 5;
                    }
                }
                break;
            //........

        }
        switch (e.getKeyChar()) {
            case 'r':
                angulo += 45;
                break;
            case 't':
                tonalizacao = tonalizacao == GL2.GL_SMOOTH ? GL2.GL_FLAT : GL2.GL_SMOOTH;
                break;
            // liga / desliga luz
            case 'l':
                if (liga) {
                    liga = false;
                } else {
                    liga = true;
                }
                System.out.println(liga);
                break;
            case 'w':
                if (modo == (GL2.GL_FILL)) {
                    modo = GL2.GL_LINE;
                } else {
                    modo = GL2.GL_FILL;
                }
                break;
            case 'd':
                if (!menu) {
                    if (x < 85) {
                        x = x + 5;
                    }
                    System.out.println("X = " + x);
                }
                break;
            case 'a':
                if (!menu) {
                    if (x > -85) {
                        x = x - 5;
                    }

                    System.out.println("X = " + x);
                }
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
