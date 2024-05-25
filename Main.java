package application;
	
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
	
	//atributos essenciais
	static int velocidade = 5; //velocidade do caminhãozinho
	static int corobjeto = 0; //cor dos objetos
	static int width =20; //largura da tela
	static int height = 20; //altura da tela
	static int objetoX = 0; //posição do objeto em X
	static int objetoY = 0; //posição do objeto em Y
	static int cantoTam = 25; //tamanho da borda da tela
	static List <Canto> truck = new ArrayList<>(); //matriz do corpo do caminhãozinho
	static Dir direcao = Dir.left; //direção que o caminhão irá seguir
	static boolean gameOver = false; //fim de jogo
	static Random rand = new Random(); //definição para o aletório dos objetos e cores

	//definindo as direções em plano 2D
	public enum Dir {
		left, right, up, down
	}
	//tamanho da tela seguindo as bordas
	public static class Canto {
		int x;
		int y;
		
		public Canto(int x, int y) {
			this.x = x;
			this.y = y;
			
		}
	}
	//classe padrão do JavaFX
	public void start(Stage primaryStage) {
		try {
			novoObjeto(); //geração de novo objeto
			
			VBox root = new VBox();
			Canvas c = new Canvas(width*cantoTam, height*cantoTam);
			GraphicsContext gc = c.getGraphicsContext2D();
			root.getChildren().add(c);
			//definição para a matriz do caminhão se deslocar pelo plano
			new AnimationTimer() {
				long lastTick = 0;
				
				public void handle (long now) {
					
					if(lastTick == 0) {
						lastTick = now;
						tick(gc);
						return;
					}
					if(now - lastTick > 1000000000 / velocidade) {
						lastTick = now;
						tick(gc);
					}
					
				}
				
			}.start();
			
			//criando a tela do JavaFX
			Scene scene = new Scene(root,width*cantoTam,height*cantoTam);
			
			//controles
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if(key.getCode() == KeyCode.W) {
					direcao = Dir.up;
				}
				if(key.getCode() == KeyCode.S) {
					direcao = Dir.down;
				}
				if(key.getCode() == KeyCode.A) {
					direcao = Dir.left;
				}
				if(key.getCode() == KeyCode.D) {
					direcao = Dir.right;
				}
			});
			
			//partes do caminhãozinho
			truck.add(new Canto(width/2, height/2));
			truck.add(new Canto(width/2, height/2));
			truck.add(new Canto(width/2, height/2));
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Projeto Reciclagem!");
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//estilizando o fim de jogo
	public static void tick(GraphicsContext gc) {
		if(gameOver) {
			gc.setFill(Color.RED);
			gc.setFont(new Font("Calibri", 50));
			gc.fillText("GAME OVER!", 100, 250);
			return;
		}
		//caminhãozinho ganhando blocos
		for(int i = truck.size() - 1; i >= 1; i--) {
			truck.get(i).x = truck.get(i-1).x;
			truck.get(i).y = truck.get(i-1).y;
		}
		//definindo a colisão do caminhãozinho
		switch(direcao) {
		case up:
			truck.get(0).y--;
			if(truck.get(0).y <0) {
				gameOver = true;
			}
			break;
		case down:
			truck.get(0).y++;
			if(truck.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			truck.get(0).x--;
			if(truck.get(0).x <0) {
				gameOver = true;
			}
			break;
		case right:
			truck.get(0).x++;
			if(truck.get(0).x > width) {
				gameOver = true;
			}
			break;
			
		}
		
		//coletar Objeto
		if(objetoX == truck.get(0).x && objetoY == truck.get(0).y) {
			truck.add(new Canto(-1, -1));
			novoObjeto();
		}
		
		//batida contra si mesmo
		for(int i = 1; i < truck.size(); i++) {
			if(truck.get(0).x == truck.get(i).x && truck.get(0).y == truck.get(i).y) {
				gameOver = true;
			}
		}
		//cor do fundo e preenchimento
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width*cantoTam, height*cantoTam);
		
		//pontuação
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("Calibri", 30));
		gc.fillText("Pontuação: " +(velocidade-6), 10, 30);
		
		//cor aleatória do Objeto
		Color cc = Color.WHITE;
		
		switch(corobjeto) {
		
		case 0: cc = Color.RED;
		break;
		case 1: cc = Color.GREEN;
		break;
		case 2: cc = Color.YELLOW;
		break;
		case 3: cc = Color.BLUE;
		break;
		case 4: cc = Color.PURPLE;
		break;
		}
		
		gc.setFill(cc);
		gc.fillOval(objetoX*cantoTam, objetoY*cantoTam, cantoTam, cantoTam);
		
		//caminhãozinho
		for(Canto c: truck) {
			gc.setFill(Color.LIGHTGRAY);
			gc.fillRect(c.x * cantoTam, c.y * cantoTam, cantoTam-1, cantoTam-1);
			gc.setFill(Color.GRAY);
			gc.fillRect(c.x * cantoTam, c.y * cantoTam, cantoTam-2, cantoTam-2);
			
		}
		
	}
	
	
	
	//criação dos Objetos no mapa
	public static void novoObjeto() {
		start: while(true) {
			objetoX = rand.nextInt(width);
			objetoY = rand.nextInt(height);
			
			for (Canto c : truck) {
				if(c.x == objetoX && c.y == objetoY) {
					continue start;
				}
			}
			
			corobjeto = rand.nextInt(5);
			velocidade++;
			break;
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
