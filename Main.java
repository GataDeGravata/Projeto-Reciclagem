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
	static int velocidade = 5;
	static int corobjeto = 0;
	static int width =20;
	static int height = 20;
	static int objetoX = 0;
	static int objetoY = 0;
	static int cantoTam = 25;
	static List <Canto> snake = new ArrayList<>();
	static Dir direcao = Dir.left;
	static boolean gameOver = false;
	static Random rand = new Random();
		
	public enum Dir {
		left, right, up, down
	}
	
	public static class Canto {
		int x;
		int y;
		
		public Canto(int x, int y) {
			this.x = x;
			this.y = y;
			
		}
	}
	
	public void start(Stage primaryStage) {
		try {
			novoObjeto();
			
			VBox root = new VBox();
			Canvas c = new Canvas(width*cantoTam, height*cantoTam);
			GraphicsContext gc = c.getGraphicsContext2D();
			root.getChildren().add(c);
			
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
			
			
			Scene scene = new Scene(root,width*cantoTam,height*cantoTam);
			
			//Controles
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
			
			//Partes do caminhãozinho
			snake.add(new Canto(width/2, height/2));
			snake.add(new Canto(width/2, height/2));
			snake.add(new Canto(width/2, height/2));
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Projeto Reciclagem!");
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Tick (velocidade e aleatório)
	public static void tick(GraphicsContext gc) {
		if(gameOver) {
			gc.setFill(Color.RED);
			gc.setFont(new Font("Calibri", 50));
			gc.fillText("GAME OVER!", 100, 250);
			return;
		}
		
		for(int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i-1).x;
			snake.get(i).y = snake.get(i-1).y;
		}
		
		switch(direcao) {
		case up:
			snake.get(0).y--;
			if(snake.get(0).y <0) {
				gameOver = true;
			}
			break;
		case down:
			snake.get(0).y++;
			if(snake.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(0).x--;
			if(snake.get(0).x <0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(0).x++;
			if(snake.get(0).x > width) {
				gameOver = true;
			}
			break;
			
		}
		
		//Coletar Objeto
		if(objetoX == snake.get(0).x && objetoY == snake.get(0).y) {
			snake.add(new Canto(-1, -1));
			novoObjeto();
		}
		
		//Batida contra si mesmo
		for(int i = 1; i < snake.size(); i++) {
			if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;
			}
		}
		//Cor do fundo e preenchimento
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width*cantoTam, height*cantoTam);
		
		//Pontuação
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("Calibri", 30));
		gc.fillText("Pontuação: " +(velocidade-6), 10, 30);
		
		//Cor aleatória do Objeto
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
		
		//Caminhãozinho
		for(Canto c: snake) {
			gc.setFill(Color.LIGHTGRAY);
			gc.fillRect(c.x * cantoTam, c.y * cantoTam, cantoTam-1, cantoTam-1);
			gc.setFill(Color.GRAY);
			gc.fillRect(c.x * cantoTam, c.y * cantoTam, cantoTam-2, cantoTam-2);
			
		}
		
	}
	
	
	
	//Criação dos Objetos no mapa
	public static void novoObjeto() {
		start: while(true) {
			objetoX = rand.nextInt(width);
			objetoY = rand.nextInt(height);
			
			for (Canto c : snake) {
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
