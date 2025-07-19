package com.example;
import java.util.Random;
import java.util.Scanner;

//---Summary---

 // Hammurabi is a text-based game where you play as the ruler of ancient Sumer.
 // Your goal is to manage resources, make decisions about land, food, and population,
 // and survive for a ten-year term while dealing with challenges like famine, plague, and rats.

 // The game involves making strategic decisions based on the state of your kingdom,
 // including buying and selling land, feeding your people, and managing the harvest.
 // You must balance the needs of your population with the resources available to you,
 // while also dealing with random events that can affect your kingdom's stability.


//---Hammurabi Blueprint---

// Random => used so we can generate random numbers for events like plague, rats, and harvest yield
// Scanner => used to get user input for decisions like how much land to buy, how much food to feed the people, etc.

public class Hammurabi {

    Random rand = new Random();

    Scanner scanner = new Scanner(System.in);

    //------Main Method-----

    // This is the starting point of the game. It creates an instance of Hammurabi
    // How we start the game; .playGame calls the play game method

    public static void main(String[] args) {
        
        new Hammurabi().playGame(); 
    }

    // ------Play Game Method-----

    // This is where the actual game steps are written
    // Everything inside these curly braces {} is what happens when you playGame()

    public void playGame() {

        //declare local variables -> store the state of the game (only exist while the game is running)
        //Think of them like stats or a scoreboard for the game

        int year = 1; //current year in the game
        int acresOwned = 1000; //acres of land owned
        int landPrice = 19; //price of land per acre
        int population = 100; //current population
        int bushelsInStorage = 2800; //bushels of grain in storage

        //These are variables to remember what happened in the previous year

        int prevPeopleStarved = 0; // number of people who starved last year
        int prevNewImmigrants = 5; // number of new immigrants last year
        int prevHarvestedBushels=3000; // total bushels harvested last year
        int prevYieldPerAcre = 3; //yield per acre last year
        int prevRatsAte=200; //amount of grain eaten by rats last year


        int totalStarvedOverTerm = 0; //total number of people who starved over the game term (accumulates)

        //This is a "switch" that tells us if the game should stop. It's currently false, meaning the game is not over yet

        boolean gameOver=false;

        //Print the welcome message
        System.out.println("Welcome to Hammurabi!");
        System.out.println("Congratulations, you are the newest ruler of ancient Sumer, elected for a ten year term of office.");
        System.out.println("Your duties are to dispense food, direct farming, and buy and sell land as needed to support your people.");
        System.out.println("Watch out for rat infestations and the plague! Grain is the general currency, measured in bushels.");
        System.out.println("\n--- Game Start ! ---");


        // ------ Main Game loop ------

        // The game continues until the player has played for 10 years
        // Think of it like: Keep doing everything inside these curly braces {} as long as the year is 10 or less


        while (year <= 10) {

            //Print the summary of the previous year
            printSummary(year, prevPeopleStarved, prevNewImmigrants, population, prevHarvestedBushels, prevYieldPerAcre, prevRatsAte, bushelsInStorage, acresOwned, landPrice);

            
            // These lines create temporary storage spots (variables) for the decisions the player will make this year. They start at zero.
            // Initiate variables for user input

            int acresToBuy = 0;
            int acresToSell = 0;
            int bushelsToFeed = 0;
            int acresToPlant = 0;


            //----- Players Decisions ----

            // Ask player if they want to buy -> how many acres to buy
            // if (acresToBuy > 0): If the player decided to buy any land (more than 0 acres)
            // bushelsInStorage = bushelsInStorage - (acresToBuy * landPrice);: take the cost of the land out of their grain storage
            // acresOwned = acresOwned + acresToBuy;: add those new acres to the land they own
            // else: "Otherwise (if they didn't buy any land, meaning acresToBuy was 0 or less)...
            // acresToSell = askHowManyAcresToSell(acresOwned);: so instead, how many acres do you want to sell?
            // bushelsInStorage = bushelsInStorage + (acresToSell * landPrice); add the grain from selling land back into storage
            // acresOwned = acresOwned - acresToSell;: remove those acres from the land they own

            acresToBuy = askHowManyAcresToBuy(landPrice, bushelsInStorage);
            if (acresToBuy > 0) {
                bushelsInStorage = bushelsInStorage - (acresToBuy * landPrice);
                acresOwned = acresOwned + acresToBuy;
            } else {
                // if dont buy -> ask player how many acres to sell
                acresToSell = askHowManyAcresToSell(acresOwned);
                bushelsInStorage = bushelsInStorage + (acresToSell * landPrice);
                acresOwned = acresOwned - acresToSell;
            }

            // How much grain do you want to feed your people?
            // The result is stored in bushelsToFeed, and then that amount is removed from bushelsInStorage

            bushelsToFeed = askHowMuchGrainToFeedPeople(bushelsInStorage);
            bushelsInStorage = bushelsInStorage - bushelsToFeed;

            // How many acres do you want to plant with grain?
            // The answer is stored in acresToPlant
            //We then remove 2 bushels of grain for every acre planted from bushelsInStorage (because planting costs grain or aka seeds)

            acresToPlant = askHowManyAcresToPlant(acresOwned, population, bushelsInStorage);
            bushelsInStorage = bushelsInStorage - (acresToPlant * 2); // 2 bushels per acre to plant

    
            //------Yearly Calculations & Random Events ------

            // plagueDeaths helper function => calculate if anyone dies from plague
            // The number of deaths is stored in plagueDeathsThisYear
            // That amount is removed from the population

            int plagueDeathsThisYear=plagueDeaths(population);
            population = population - plagueDeathsThisYear;

            // starvationDeaths to calculate the number of people who starved
            // totalStarvedOverTerm = totalStarvedOverTerm + starvedThisYear; => adds the number of people who starved this year to the running totalStarvedOverTerm count.
            // population = population - starvedThisYear;: The starved people are removed from the population

            int starvedThisYear = starvationDeaths(population, bushelsToFeed);
            totalStarvedOverTerm= totalStarvedOverTerm + starvedThisYear;
            population = population - starvedThisYear;

            // check if there is an uprising
            // if(uprising(population, starvedThisYear)): If the uprising function says yes, there's an uprising
            // System.out.println(...): "...print a message that the player has been overthrown!
            // gameOver = true;: "...set our gameOver switch to true to indicate the game is finished
            // break; => immediately stop the game loop.

            if(uprising(population, starvedThisYear)) {
                System.out.println("O great Hammurabi, your people are revolting! You have been overthrown!");
                gameOver = true; //end the game
                break; //exit the game loop immediately
            }

            // immigrants arrive if no one starved that year
            // immigrantsThisYear = immigrants(...): calculate how many new immigrants arrive
            // population = population + immigrantsThisYear;: "...and add those new immigrants to the population

            int immigrantsThisYear=0;
            if(starvedThisYear == 0) {
                immigrantsThisYear = immigrants(population,acresOwned,bushelsInStorage);
                //add immigrants
                population = population + immigrantsThisYear;
            }


            // calculate harvest total
            // This calls the harvest helper function
            // The harvestedBushels are then added to bushelsInStorage

            int harvestedBushels = harvest(acresToPlant, acresToPlant*2);
            bushelsInStorage = bushelsInStorage + harvestedBushels;

            // calculate grain eaten by rats
            // This calls grainEatenByRats
            // The amount they ate (ratsAteThisYear) is then removed from bushelsInStorage

            int ratsAteThisYear = grainEatenByRats(bushelsInStorage);
            bushelsInStorage = bushelsInStorage - ratsAteThisYear;

            // get land price for next year
            // The price of land can change
            // Let's get the new price of land for the next year.

            landPrice = newCostOfLand();


            ///----- Update previous year variables -----

            /// Important for the printSummary function
            /// They update the "previous year" variables with what just happened in the current year
            /// When the loop starts again for the next year, the summary can correctly show what happened in the year that just finished
            
            prevPeopleStarved= starvedThisYear;
            prevNewImmigrants = immigrantsThisYear;
            prevYieldPerAcre= harvestedBushels / acresToPlant;
            prevHarvestedBushels=harvestedBushels;
            prevRatsAte= ratsAteThisYear;

            // increment year by 1
            // We're done with this year! So let's move to the next year
            // This is what eventually makes the while (year <= 10) loop stop after 10 years

            year++;

        } // This curly brace marks the end of the game loop => loop goes back up to while (year <= 10) to check if it should run another year


        //-----Finals Summary------

        // The game loop has finished
        // Was it because 10 years passed, or because of an uprising?
        // if(!gameOver): If the gameOver switch is false (meaning the game was not ended by an uprising, so it must have completed 10 years)
        // finalSummary(...): => call the finalSummary function to print the end-of-game report.

        if(!gameOver) {
            finalSummary(totalStarvedOverTerm, acresOwned, population);
        }

        // We're done with reading from the keyboard, so let's close the scanner tool
        // This is good practice to clean up resources.

        scanner.close();
    }

    //-----Get Input From The User----- 

    // getNumber() -> Getting Valid Numbers from the Player
    // Helper function that's designed to always get a whole number from the player
    // It takes a message (like "How many acres?") that it will print &  promises to give back an int (a whole number)
    // While loop -> use this  because we want to keep asking the user for input until they give us a valid number
    // System.out.print(); -> Print the message we were given (like 'How many acres to buy?')
    // We use print (not println), so the cursor stays on the same line for the player to type
    // If scanner.hasNextInt -> look at what the player just typed. Is it a whole number?
    // return scanner.nextInt -> If it is a whole number, then grab that number and give it back (return) to whoever asked for it
    // Once we return, we exit this function, and the while (true) loop also stops

    int getNumber(String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else { // If what the player typed was NOT a whole number; print an error message
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); //  Get rid of whatever invalid text they typed  so it doesn't confuse the program on the next try
                               //   The while (true) then loops back to the beginning, asking the message again
            }
        }
    }


    //-----Player Decisions Methods-----

    // These functions use getNumber() to get input and add extra rules to make sure the player's choices make sense
    // askHowManyAcresToBuyc -> asks the user to buy land using the getNumber helper
    // if (acresToBuy < 0): You can't buy negative land
    // else if ((acresToBuy * landPrice) > bushelsInStorage): You can't buy land if you don't have enough grain to pay for it

    int askHowManyAcresToBuy(int landPrice, int bushelsInStorage) {
     
       while (true) {
           int acresToBuy = getNumber("O great Hammurabi, how many acres shall you buy? (Current price: " + landPrice + " bushels/acre, You have: " + bushelsInStorage + " bushels): ");
           if (acresToBuy < 0) {
               System.out.println("O Great Hammurabi, you cannot buy a negative amount of land!");
           } else if ((acresToBuy * landPrice) > bushelsInStorage) {
               System.out.println("O Great Hammurabi, surely you jest! We have only " + bushelsInStorage + " bushels left!");
           } else {
                return acresToBuy;
            }
        }
    }

    // Asks the user how many acres they want to sell
    // if (acresToSell < 0): You can't sell negative land
    // else if (acresToSell > acresOwned): You can't sell more land than you actually possess

    int askHowManyAcresToSell(int acresOwned) {
        
        while (true) {
            int acresToSell = getNumber("O great Hammurabi, how many acres shall you sell? (You own: " + acresOwned + " acres): ");
            if (acresToSell < 0) {
                System.out.println("O Great Hammurabi, you cannot sell a negative amount of land!");
            } else if (acresToSell > acresOwned) {
                System.out.println("O Great Hammurabi, you cannot sell more land than you own!");
            } else {
                return acresToSell;
            }
        }
    }

    // Asks the user how much grain to feed
    // if (bushelsToFeed < 0): You can't feed negative grain
    // else if (bushelsToFeed > bushelsInStorage): You can't feed more grain than you have in storage

    int askHowMuchGrainToFeedPeople(int bushelsInStorage) {
        
        while (true) {
            int bushelsToFeed = getNumber("O great Hammurabi, how much grain shall you feed the people? (You have: " + bushelsInStorage + " bushels): ");
            if (bushelsToFeed < 0) {
                System.out.println("O Great Hammurabi, you cannot feed a negative amount of grain!");
            } else if (bushelsToFeed > bushelsInStorage) {
                System.out.println("O Great Hammurabi, surely you jest! We have only " + bushelsInStorage + " bushels left!");
            } else {
                return bushelsToFeed;
            }
        }
    }

    // asks the user how many acres to plant
    // if (acresToPlant < 0) -> Can't plant negative land
    // else if (acresToPlant > acresOwned) -> Can't plant more land than you own
    // else if (acresToPlant * 2 > bushelsInStorage) -> It costs 2 bushels of grain to plant each acre (acresToPlant * 2)
    // You can't plant if you don't have enough grain for the seeds
    // else if (acresToPlant > population * 10) -> Each person can only tend to 10 acres (population * 10)
    // You can't plant if you don't have enough people to work the land

    int askHowManyAcresToPlant(int acresOwned, int population, int bushelsInStorage) {
       
        while(true) {
            int acresToPlant = getNumber("O great Hammurabi, how many acres shall you plant? (You own: " + acresOwned + " acres, You have: " + bushelsInStorage + " bushels, Population: " + population + "): ");
            if (acresToPlant < 0) {
                System.out.println("O Great Hammurabi, you cannot plant a negative amount of land!");
            } else if (acresToPlant > acresOwned) {
                System.out.println("O Great Hammurabi, you cannot plant more land than you own!");
            } else if (acresToPlant * 2 > bushelsInStorage) { // 2 bushels per acre
                System.out.println("O Great Hammurabi, you only have " + bushelsInStorage + " bushels to plant " + acresToPlant + " acres! You need " + (acresToPlant * 2) + " bushels.");
            } else if (acresToPlant > population * 10) {
                System.out.println("O Great Hammurabi, you do not have enough people to tend to that many acres!");
            } else {
                return acresToPlant;
            }
        }
    }


    //-----Game Calculation Methods-----

    // These functions figure out what happens in the city based on various conditions and random chances
    // Calculate the number of deaths due to plague
    // rand.nextInt(100) -> This gets a random whole number between 0 and 99
    // if(rand.nextInt(100) < 15) -> If random number is less than 15, then there's a plague; this happens 15% of the time
    // return population / 2; -> If there's a plague, half of the population dies; return that number
    // return 0; -> If there's no plague (the random number was 15 or higher), then 0 people die from plague

    public int plagueDeaths (int population) {
        
        if(rand.nextInt(100) < 15) { // 15%
            return population / 2; // 50% 
        }
       
        return 0; 
    }

    // Calculate the number of deaths due to starvation
    // int grainNeeded = population * 20;-> Every person needs 20 bushels to live
    // Multiply the population by 20 to find out how much grainNeeded in total
    // if (bushelsInStorage < grainNeeded) -> If the bushelsInStorage are less than the grainNeeded (not enough food)...
    // int starvedPeople = (grainNeeded - bushelsInStorage +19) / 20; -> calculate how many people starved
    // subtract the bushelsInStorage from grainNeeded to see the shortage
    // 19 / 20 => special math trick to make sure we round up how many people starve
    // Even if you're just a little bit short for one person, that person still starves
    // return Math.min(starvedPeople, population); -> Return the smaller number between how many people starvedPeople and the population
    // This ensures you can't have more people starve than actually exist
    // return 0;: "If there was enough grain (the if condition was false), then 0 people starve

    public int starvationDeaths(int population, int bushelsInStorage) {
        
        int grainNeeded = population * 20; 
       
        if (bushelsInStorage < grainNeeded) {
            int starvedPeople = (grainNeeded - bushelsInStorage +19) / 20;
            return Math.min(starvedPeople, population); 
        }
        
        return 0; 
    }

    // Calculate uprising -> if more than 45% of the population starves, there is an uprising
    // howManyPeopleStarved * 100 -> This is the number of starved people, converted to a percentage scale (if 10 starved => 1000)
    // population * 45 -> This is 45% of the total population, also converted to a percentage scale
    // return howManyPeopleStarved * 100 > population * 45; -> If howManyPeopleStarved (times 100) is greater than 45% of the population (times 100), then return true
    // Otherwise, return false; This checks if more than 45% of your people starved

    public boolean uprising(int population, int howManyPeopleStarved) {
       return howManyPeopleStarved * 100 > population * 45; 
    }

    // Calculate the number of immigrants that will come to the city
    // Use formula to calculate how many new people come to your city
    // Uses a combination of your land, grain, and current population
    // The + 1 ensures at least one immigrant if the other parts of the formula result in 0 (unless the population is huge and resources are tiny).

    public int immigrants(int population, int acresOwned, int grainInStorage) {
        return (20 * acresOwned + grainInStorage) / (100 * population) + 1;
    }

    // Calculate the harvest yield per acre
    // rand.nextInt(6) -> Gets a random number between 0 and 5
    // + 1 -> Makes it a random number between 1 and 6. This is the yieldPerAcre (how much grain grows on one acre)
    // return acres * yieldPerAcre; -> "Multiply the acres you planted by how much yieldPerAcre you got to find your total harvest.

    public int harvest(int acres, int bushelsUsedAsSeed) {
        
        int yieldPerAcre = rand.nextInt(6) + 1; 
       
        return acres * yieldPerAcre; 
    }

    // Calculate grain eaten by rats
    // if (rand.nextInt(100) < 40): There's a 40% chance of a rat infestation (if the random number is less than 40)
    // int percentEaten = 10 + rand.nextInt(21); -> If there is an infestation, the rats eat a random percentage of grain
    // rand.nextInt(21) gives a number from 0 to 20
    // Adding 10 makes it 10 to 30. So, they'll eat between 10% and 30% of your grain
    // return percentEaten; -> Return the percentage that was eaten
    // return 0; -> If there's no infestation, 0 grain is eaten by rats

    public int grainEatenByRats(int bushels) {
       
        if (rand.nextInt(100) < 40) { 
            
            int percentEaten = 10 + rand.nextInt(21); // 10% to 30%
            return percentEaten;
        }
        
        return 0;
    }

    // Calculate the new cost of land
    // rand.nextInt(7): Gets a random number from 0 to 6
    // Price of land is random but ranges from 17 to 23 --> (7 numbers in between 17 and 23)
    // + 17: Adds 17 to that number. So, the result will be a random price between 17 (0+17) and 23 (6+17)
    // This is the new random price for an acre of land

    public int newCostOfLand() {
        
        return rand.nextInt(7) + 17;

    }

    //-----Random Number Generation Methods-----

    // generate a random number between min and max --> inclusive so +1
    // max - min + 1: Calculates how many possible numbers are in your range
    // rand.nextInt(...) -> Gets a random number from 0 up to (but not including) that count
    // + min -> Adds the min value to shift the range correctly
    // If we want 17-23, and rand.nextInt(7) gives 0, then 0 + 17 = 17
    // This correctly gives a random number between min and max, including both min and max

    public int getRandomNumber(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }


    //-----SUMMARY METHODS FOR PRINTING OUTPUT -----

    // Print the summary of the year; like a yearly report 

    void printSummary(int year, int peopleStarvedPrev, int newImmigrantsPrev, int currentPopulation,
                      int totalHarvestedPrev, int yieldPerAcrePrev, int ratsAtePrev,
                      int currentBushelsInStorage, int currentAcresOwned, int currentLandPrice) {
        System.out.println("\nO great Hammurabi!");
        System.out.println("You are in year " + year + " of your ten year rule.");
        System.out.println("In the previous year " + peopleStarvedPrev + " people starved to death.");
        System.out.println("In the previous year " + newImmigrantsPrev + " people entered the kingdom.");
        System.out.println("The population is now " + currentPopulation + ".");
        System.out.println("We harvested " + totalHarvestedPrev + " bushels at " + yieldPerAcrePrev + " bushels per acre.");
        System.out.println("Rats destroyed " + ratsAtePrev + " bushels, leaving " + currentBushelsInStorage + " bushels in storage.");
        System.out.println("The city owns " + currentAcresOwned + " acres of land.");
        System.out.println("Land is currently worth " + currentLandPrice + " bushels per acre.");
    }

    // Print final summary at the end of the game (if you made it for 10 years)
    
    void finalSummary(int totalStarvedOverTerm, int finalAcresOwned, int finalPopulation) {
        System.out.println("\n---Game Over!---");
         System.out.println("After 10 years, your rule has ended.");
        System.out.println("Total people starved over the 10 years: " + totalStarvedOverTerm);
        System.out.println("Final population: " + finalPopulation);
        System.out.println("Final acres owned: " + finalAcresOwned);
    }
}