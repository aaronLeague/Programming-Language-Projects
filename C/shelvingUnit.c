#include <stdio.h>
#include <stdlib.h>
#include <memory.h>

struct Item{
    char name[50];
    int dollars;
    int cents;
};

int main() {

    //Read shelf dimensions from user and confirm them
    printf("Welcome to your shelf unit!\n\nPlease enter the number of shelves and slots:\n");
    int* shelves = malloc(sizeof(int));
    int* slots = malloc(sizeof(int));
    scanf("%d %d", shelves, slots);
    printf("You have requested:\n%d shelves with %d slots per shelf.", *shelves, *slots);

    //Initialize 2D array of shelf locations
    struct Item** shelfUnit = malloc(sizeof(struct Item*) * *shelves);
    for (int i = 0; i < *shelves; i++){
       shelfUnit[i] = malloc(sizeof(struct Item) * *slots);
    }

    printf("\n\nTo add an item, enter its one-word name,the dollars of its price, the cents of its price,");
    printf("\nthe shelf and slot it is on. (e.g. for a $1.89 pen on slot 3 of shelf 5: \"ballpoint_pen 1 89 3 5\")");
    printf("\nWhen you are done, enter \"d\" for the item name.\n");

    //Variables to hold user inputs
    char* name = malloc(sizeof(char) * 100);
    int* dollar = malloc(sizeof(int));
    int* cent = malloc(sizeof(int));
    int* shelf = malloc(sizeof(int));
    int* slot = malloc(sizeof(int));
    char done[] = "d";

    //Allow user to populate shelf spaces
    while(strcmp(name, done) != 0){
        printf("\nItem name:\n");
        scanf("%s", name);

        //Check if user has entered the 'done' signal
        if(strcmp(name, done) != 0) {
            printf("dollars cents:\n");
            scanf("%d %d", dollar, cent);
            printf("shelf slot:\n");
            scanf("%d %d", shelf, slot);

            //Make sure user input is reasonable
            if(*shelf > 0 && *shelf <= *shelves && *slot > 0 && *slot <= *slots && *cent >= 0 && *cent < 100) {
                struct Item temp;
                strcpy(temp.name, name);
                temp.dollars = *dollar;
                temp.cents = *cent;
                shelfUnit[*shelf - 1][*slot - 1] = temp;
            }else{
                //If user input is unreasonable, tell them so
                printf("\nINVALID ENTRY\n");
            }
        }
    }

    printf("\nTo see what is on a shelf, enter the shelf and slot numbers.");
    printf("\nWhen you are finished, type \"0 0\" to exit.\n");

    //Print item at user-given shelf location
    *shelf = 1;
    while(*shelf != 0){

        //Read in shelf location
        printf("\nshelf slot:\n");
        scanf("%d %d", shelf, slot);

        //Check to make sure user input is reasonable and if the 'exit' command has been given
        if(*shelf > 0 && *shelf <= *shelves && *slot > 0 && *slot <= *slots) {
            printf("Item in shelf %d slot %d:", *shelf, *slot);

            //Check for a price to see if the given shelf location is empty
            if (shelfUnit[*shelf - 1][*slot - 1].dollars != 0 || shelfUnit[*shelf - 1][*slot - 1].cents != 0) {

                //Print info for item at non-empty location
                printf("\nItem name: %s\n", shelfUnit[*shelf - 1][*slot - 1].name);
                printf("Price: $%d.%d\n", shelfUnit[*shelf - 1][*slot - 1].dollars,
                       shelfUnit[*shelf - 1][*slot - 1].cents);
            } else {
                printf("\nNO ITEM HERE\n");
            }
        }else if(*shelf == 0){
            printf("\nGoodbye!\n");
        }else{
            printf("\nWoops! You missed the shelf. Try again.\n");
        }
    }

    return 0;
}
