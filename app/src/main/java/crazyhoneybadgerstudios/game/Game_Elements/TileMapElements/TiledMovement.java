package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import crazyhoneybadgerstudios.util.Vector2;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a movement behaviour allowing the entities to move grid by grid
 */

public class TiledMovement extends Movement {
    /**
     * constructor setting the target object
     * @param target
     */
    public TiledMovement(Entity target){
        super(target);
    }

    /**
     * overwritten move definition to include new movement logic
     * @param direction
     */
    @Override
    public void move(Direction direction){
        target.setDirection(direction);
        //now move position by 32 units in each direction
        if(isMoving){
            if(direction == Direction.UP)
            {
                target.position = new Vector2(target.position.x, target.position.y + 32f);
            }
            else if(direction == Direction.DOWN)
            {
                target.position = new Vector2(target.position.x, target.position.y - 32f);
            }
            else if(direction == Direction.RIGHT)
            {
                target.position = new Vector2(target.position.x + 32f, target.position.y);
            }
            else if(direction == Direction.LEFT)
            {
                target.position = new Vector2(target.position.x - 32f, target.position.y);
            }
            stop();
        }
    }
}
