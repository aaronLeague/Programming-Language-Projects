#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

struct tapeNode{
    char content;
    struct tapeNode *next;
    struct tapeNode *prev;
};

struct arrIndex{
    char w;
    bool rl, notEmpty;
    int state;
};

struct tapeNode *addNode(struct tapeNode *n, bool forward){
    struct tapeNode *m = malloc(sizeof(struct tapeNode));
    if(forward) {
        n->next = m;
        m->prev = n;
        m->next = NULL;
    }else{
        n->prev = m;
        m->next = n;
        m->prev = NULL;
    }
    m->content = 'B';
    return m;
};

struct tapeNode* step(struct tapeNode* n, bool forward, struct tapeNode** head){
    if(forward){
        if(n->next == NULL){
            n = addNode(n, true);
        }else{
            n = n->next;
        }
    }else{
        if(n->prev == NULL){
            n = addNode(n, false);
            *head = n;
        }else{
            n = n->prev;
        }
    }
    return n;
}

void freeList(struct tapeNode* h){
    struct tapeNode* tmp;

    while (h != NULL){
        tmp = h;
        h = h->next;
        tmp->next = NULL;
        tmp->prev = NULL;
        free(tmp);
    }
}

void testList(){
    struct tapeNode *cu = malloc(sizeof(struct tapeNode));
    struct tapeNode* he = cu;

    for (int i = 0; i <= 10; i++){
        cu->content = (char) (i + 97);
        cu = addNode(cu, true);
    }
    cu = he;

    while (cu->content != 'B'){
        printf("%c, ", cu->content);
        cu = step(cu, true, &he);
    }

    cu = he;
    printf("\n");
    for (int i = 0; i < 20; i++){
        printf("%c, ", cu->content);
        cu = step(cu, true, &he);
        cu = step(cu, true, &he);
        cu = step(cu, true, &he);
        cu = step(cu, false, &he);
    }

    freeList(he);
    cu = NULL;
    he = NULL;
}

void printList(struct tapeNode* point, struct tapeNode** hPoint){
    while (point->next != NULL){
        printf("%c", point->content);
        point = step(point, true, hPoint);
    }
    printf("%c\n", point->content);
}

int main() {

    //testList();

    FILE *instr;
    instr = fopen("instructions" , "r");

    //read initial tape content (build tape)
    char *readTape = malloc(sizeof(char) * 100);
    fgets(readTape, 100, instr);

    struct tapeNode *curr = malloc(sizeof(struct tapeNode));
    curr->content = readTape[0];
    struct tapeNode* head = curr;

    for (int i = 1; readTape[i] != '\n'; i++){
        curr = addNode(curr, true);
        curr->content = readTape[i];
    }
    curr = head;
    free(readTape);
    readTape = NULL;


    //print tape contents
    printf("\nINITIAL TAPE CONTENTS:\n");
    printList(head, &head);


    //read number of states
    int *readLn = malloc(sizeof(int));
    fscanf(instr, "%d", readLn);
    int numStates = readLn[0];
    *readLn = 0;
    printf("\nNUMBER OF STATES: %d\n", numStates);


    //read start state
    fscanf(instr, "%d", readLn);
    int *currState = malloc(sizeof(int));
    *currState = *readLn;
    printf("\nSTARTING STATE: %d\n", *currState);


    //read end state
    fscanf(instr, "%d\n", readLn);
    int endState = *readLn;
    printf("\nTERMINAL STATE: %d\n", endState);
    free(readLn);
    readLn = NULL;


    //build array to hold instructions
    struct arrIndex** stateArr = malloc(sizeof(struct arrIndex*) * numStates);
    for (int i = 0; i < numStates; i++){
        stateArr[i] = malloc(sizeof(struct arrIndex) * 256);
    }


    //read instructions
    char *readInstr = malloc(sizeof(char) * 11);
    printf("\nINTERPRETED INSTRUCTIONS:\n");
    while(fgets(readInstr, 11, instr) != NULL){

        char *temp;
        int state = (int) strtol(&readInstr[0], &temp, 10);

        stateArr[state][(int) readInstr[2]].w = readInstr[4];


        stateArr[state][(int) readInstr[2]].state = (int) strtol(&readInstr[8], &temp, 10);
        //free(temp);
        //temp = NULL;

        if (readInstr[6] == 'R'){
            stateArr[state][(int) readInstr[2]].rl = true;
        }else if (readInstr[6] == 'L'){
            stateArr[state][(int) readInstr[2]].rl = false;
        }else{
            printf("\nERROR READING R/L FROM FILE!\n");
        }

        stateArr[state][(int) readInstr[2]].notEmpty = true;

        printf("%d %d %c %s %d\n", state, readInstr[2], stateArr[state][(int) readInstr[2]].w,
                (stateArr[state][(int) readInstr[2]].rl ? "true" : "false"),
                stateArr[state][(int) readInstr[2]].state);
    }
    free(readInstr);
    readInstr = NULL;

    fclose(instr);

    //Print instruction array contents
//    for (int i = 0; i < 256; i++) {
//        printf("%d ", i);
//        for (int j = 0; j < numStates; j++){
//            printf("%c ", stateArr[j][i].w);
//        }
//        printf("\n");
//    }

    //follow the instructions until the final state is reached
    curr = head;
    int position = 0;
    printf("\nNOW EXECUTING PROGRAM\n");
    while (*currState != endState){
        printf("===================================================================\n"
               "TAPE STATUS:\n");
        printList(head, &head);
        for (int i = 0; i < position; ++i) {
            printf(" ");
        }
        printf("^\n");

        printf("Old State: %d   ", *currState);
        struct arrIndex* ci = &stateArr[*currState][(int) curr->content];
        if (ci->notEmpty) {
            printf("Reading: %c  Writing: %c  Moving: %s  ", curr->content, ci->w,
                   (ci->rl ? "right" : "left"));
            curr->content = ci->w;
            curr = step(curr, ci->rl, &head);
            *currState = ci->state;
            printf("New State: %d\n", *currState);
            position += (ci->rl ? 1 : ((position > 0) ? -1 : 0));
        }else{
            printf("Error in the state array!\n");
        }
    }


    //print final state
    curr = head;
    printf("\n===================="
           "\nFINAL TAPE CONTENTS:\n");
    printList(head, &head);
    printf("====================\n");


    //free memory
    free(currState);
    for (int i = 0; i < numStates; i++){
        free(stateArr[i]);
    }
    free(stateArr);
    freeList(head);

    return 0;
}
