package com.linda.cluster.redis.common;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        @SuppressWarnings("unused")
		Service service = new Service(){

			@Override
			public void startup() {
				
			}

			@Override
			public void shutdown() {
				
			}
        	
        };
    }
}
