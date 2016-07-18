package com.nist.ccmcl;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ReadOperation implements Runnable {

	private static LinkedBlockingQueue<String> buffer = new LinkedBlockingQueue<String>();

	final protected void produce(String s) {
		buffer.add(s);
	}

	final static private void consume() throws InterruptedException {
		Thread load_data = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					String input;
					try {

						input = buffer.take();
						int position = TestData.get_rows();
						if (TestData.get_rows() < 1) {
							Main.infile = new String[1];
							Main.test = new int[1][];
							Main.max_array_size = 0;
						}
						if (position == Main.max_array_size) {
							
							for(int i = 0; i < 5; i++)
								while(Main.tway_threads[i] != 0 && Main.tway_objects[i] != null){
									Thread.sleep(1000);
								}
						
							if (Main.max_array_size < 1000) {
								Main.max_array_size = 1000;
								Main.test = new int[Main.max_array_size][];
								Main.infile = new String[Main.max_array_size];

							} else {
								Main.test = new int[Main.test.length][];
								Main.infile = new String[Main.infile.length];
							}
							position = 0;
							System.gc();

						}

						Main.infile[position] = input;
						Main.test[position] = new int[TestData.get_columns()];
						TestData.set_rows(position + 1);
						int status = Main.setupFile(position);
						if (status == -1) {
							continue;
						} else if (status != 0) {
							System.out.println("Error: Something went wrong.\nExiting...\n");
							System.exit(status);
						}

						while (java.lang.Thread.activeCount() > Main.threadmax) {
							System.out.println("Exceeded maximum threads specified... Waiting...");
							Thread.sleep(1000);
						}

						for (int i = 0; i < 5; i++) {
							if (Main.tway_objects[i] != null) {
								switch (i) {
								case 0:
									Main.Tway("2way");
									Main.tway_threads[0]++;
									break;
								case 1:
									Main.Tway("3way");
									Main.tway_threads[1]++;
									break;
								case 2:
									Main.Tway("4way");
									Main.tway_threads[2]++;
									break;
								case 3:
									Main.Tway("5way");
									Main.tway_threads[3]++;
									break;
								case 4:
									Main.Tway("6way");
									Main.tway_threads[4]++;
									break;
								}
							}
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});
		load_data.start();

	}

	public abstract void readData();

	@Override
	public void run() {
		try {
			consume();
			readData();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
