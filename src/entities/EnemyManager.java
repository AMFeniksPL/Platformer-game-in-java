package entities;

import gamestates.Playing;
import utils.Constants;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.*;

public class EnemyManager {
    private Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();


    public EnemyManager(Playing playing){
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void addEnemies() {
        crabbies = LoadSave.GetCrabs();
    }

    public void update(int[][] lvlData){
        for (Crabby c : crabbies)
            c.update(lvlData, playing.getPlayer());
    }

    public void draw(Graphics g, int xLevelOffset){
        drawCrabs(g, xLevelOffset);
    }

    private void drawCrabs(Graphics g, int xLevelOffset){
        for (Crabby c : crabbies) {
            g.drawImage(
                    crabbyArr[c.getEnemyState()][c.getAniIndex()],
                    (int) c.getHitBox().x - xLevelOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                    (int) c.getHitBox().y - CRABBY_DRAWOFFSET_Y,
                    CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);
            c.drawHitBox(g, xLevelOffset);
            c.drawAttackBox(g, xLevelOffset);
        }
    }

    private void loadEnemyImgs() {
        crabbyArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
        for (int j = 0; j < crabbyArr.length; j++){
            for (int i = 0; i < crabbyArr[j].length; i++)
                crabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
        }
    }
}
