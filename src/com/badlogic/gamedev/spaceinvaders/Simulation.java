package com.badlogic.gamedev.spaceinvaders;

import java.util.ArrayList;

public class Simulation 
{	
	public ArrayList<Invader> invaders = new ArrayList<Invader>();
	public ArrayList<Block> blocks = new ArrayList<Block>( );
	public ArrayList<Shot> shots = new ArrayList<Shot>( );
	public Ship ship;
	public Shot shipShot = null;
	
	public Simulation( )
	{
		populate( );
	}
	
	private void populate( )
	{
		ship = new Ship();
		ship.position.set(0, 0, 0);		
		
		for( int row = 0; row < 4; row++ )
		{
			for( int column = 0; column < 8; column++ )
			{
				Invader invader = new Invader( );
				invader.position.set( -7 + column * 2f, 0, -15 + row * 1.5f );
				invaders.add( invader );
			}
		}
	}

	ArrayList<Shot> removedShots = new ArrayList<Shot>();
	public void update( float delta )
	{			
		updateInvaders( delta );
		updateShots( delta );
		checkShipCollision( );
		checkInvaderCollision( );
	}
	
	private void updateInvaders( float delta )
	{
		for( int i = 0; i < invaders.size(); i++ )
		{
			Invader invader = invaders.get(i);
			invader.update( delta );
		}
	}
	
	private void updateShots( float delta )
	{
		removedShots.clear();
		for( int i = 0; i < shots.size(); i++ )
		{
			Shot shot = shots.get(i);
			shot.update(delta);
			if( shot.hasLeftField )
				removedShots.add(shot);
		}
		
		if( shipShot != null && shipShot.hasLeftField )
			shipShot = null;
		
		if( Math.random() < 0.01 && invaders.size() > 0 )
		{
			Shot shot = new Shot( true );
			int index = (int)(Math.random() * (invaders.size() - 1));
			shot.position.set( invaders.get(index).position );
			shots.add( shot );
		}
	}
	
	private void checkInvaderCollision() 
	{	
		removedShots.clear();
		
		for( int i = 0; i < shots.size(); i++ )
		{
			Shot shot = shots.get(i);
			if( shot.isInvaderShot )
				continue;					
			
			for( int j = 0; j < invaders.size(); j++ )
			{
				Invader invader = invaders.get(j);
				if( invader.position.distance(shot.position) < 0.75f )
				{					
					removedShots.add( shot );
					shot.hasLeftField = true;
					invaders.remove(invader);
					break;
				}
			}
		}
		
		for( int i = 0; i < removedShots.size(); i++ )		
			shots.remove( removedShots.get(i) );		
	}

	private void checkShipCollision() 
	{	
		removedShots.clear();
		
		for( int i = 0; i < shots.size(); i++ )
		{
			Shot shot = shots.get(i);
			if( !shot.isInvaderShot )
				continue;
									
			if( ship.position.distance(shot.position) < 1 )
			{					
				removedShots.add( shot );
				shot.hasLeftField = true;
				ship.lives--;
				break;
			}			
		}
		
		for( int i = 0; i < removedShots.size(); i++ )		
			shots.remove( removedShots.get(i) );	
		
		for( int i = 0; i < invaders.size(); i++ )
		{
			Invader invader = invaders.get(i);
			if( invader.position.distance(ship.position) < 1 )
			{
				ship.lives--;
				invaders.remove(invader);
				break;
			}
		}
	}

	public void moveShipLeft(float delta, float scale) 
	{	
		ship.position.x -= delta * Ship.SHIP_VELOCITY * scale;
		if( ship.position.x < -13 )
			ship.position.x = -13;
	}

	public void moveShipRight(float delta, float scale ) 
	{	
		ship.position.x += delta * Ship.SHIP_VELOCITY * scale;
		if( ship.position.x > 13 )
			ship.position.x = 13;
	}

	public void shot() 
	{	
		if( shipShot == null )
		{
			shipShot = new Shot( false );
			shipShot.position.set( ship.position );
			shots.add( shipShot );
		}
	}		
}