package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverOverlay {
    private Playing playing;
    public GameOverOverlay(Playing playing){
        this.playing = playing;
    }

    public void draw(Graphics g){
        g.setColor(new Color(0, 0, 0, 200));

        g.setColor(Color.white);
        g.drawString("Game Over", Game.GAME_WIDTH / 2, 150);
        g.drawString("Press Esc to enter Main Menu!", Game.GAME_WIDTH / 2, 300);
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            playing.resetAll();
            Gamestate.state = Gamestate.MENU;
        }
    }
}
