package objects;

import main.Game;

import static utils.Constants.ObjectConstants.*;

public class GameContainer extends GameObject {
    public GameContainer (int x, int y, int objType){
        super(x, y, objType);
        createHitBox();
    }

    private void createHitBox() {
        if (objType == BOX) {
            initHitBox(25, 18);
            xDrawOffset = (int) (7 * Game.SCALE);
            yDrawOffset = (int) (12 * Game.SCALE);
        }
        else {
            initHitBox(23, 25);
            xDrawOffset = (int) (8 * Game.SCALE);
            yDrawOffset = (int) (5 * Game.SCALE);
        }
        hitBox.x += hitBox.width / 2;
        hitBox.y += yDrawOffset + (int)(Game.SCALE * 2);
    }

    public void update(){
        if (doAnimation){
            updateAnimationTick();
        }
    }
}
