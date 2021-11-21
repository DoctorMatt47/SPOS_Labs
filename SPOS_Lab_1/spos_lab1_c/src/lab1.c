#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <time.h>

#include "trialfuncs.h"
#include "utils.h"

#define MAX_ATTEMPTS_COUNT 5
#define WITHOUT_PROMPT_TIME 8
#define SELECT_TIMEOUT_SEC 0
#define SELECT_TIMEOUT_USEC 250000

//#define F_FUNC trial_f_imul
//#define G_FUNC trial_g_imul

#define F_FUNC f_func
#define G_FUNC g_func

int fIn[2];
int fOut[2];
int gIn[2];
int gOut[2];
char res = 'a';


void runProcess(int inFd, int outFd, compfunc_status_t (*func) (int, int*))
{
    int isRetry = 1;
    int x;
    result_t res;
    
    read(inFd, &x, sizeof(x));
    while (isRetry) 
    {
        res.status = func(x, &res.value);
        write(outFd, &res, sizeof(res));
        read(inFd, &isRetry, sizeof(isRetry));
    }
}

void printPrompt() 
{
    printf("(a) Continue\n(b) Continue without prompt\n(c) Stop\n");
    int c;
    while ( (c = getchar()) != '\n' && c != EOF ) { }
    scanf("%c", &res);
}

void printFuncResult(char func, int x, result_t result) {
    printf ("%c(%d): %s", func, x, symbolic_status(result.status));
    if (result.status == 0) printf("<%d>\n", result.value);
    else printf("\n");
}

void printResult(result_t fResult, result_t gResult) 
{
    if (fResult.status == COMPFUNC_HARD_FAIL 
        || gResult.status == COMPFUNC_HARD_FAIL)
    {
        printf("Result of mul operation: fail\n");
    }
    else if (fResult.status == COMPFUNC_SUCCESS 
        && gResult.status == COMPFUNC_SUCCESS)
    {
        printf("Result of mul operation: %d\n", fResult.value * gResult.value);
    }
    else if (fResult.status == COMPFUNC_STATUS_MAX 
        || gResult.status == COMPFUNC_STATUS_MAX)
    {
        printf("Result of mul operation: undefined\n");
    }
}

void handleResult(int x,
    char funcPrefixes[],
    result_t results[],
    int fdIns[],
    int fdOuts[],
    int isNested) 
{
    switch (results[0].status) 
    {
        case COMPFUNC_SOFT_FAIL: 
        {
            printFuncResult(funcPrefixes[0], x, results[0]);
            int attempt = 0;
            int isBreak = 0;
            while(1) 
            {
                int attemptsCount = (int)(WITHOUT_PROMPT_TIME /
                    (SELECT_TIMEOUT_SEC + SELECT_TIMEOUT_USEC / (float)1000000));
                for(int i = 0; i < attemptsCount; i++) 
                {
                    fd_set fOutSet = createFdSet((int[]){fdOuts[0]}, 1);
                    timeval_t tv = createTimeval(SELECT_TIMEOUT_SEC, SELECT_TIMEOUT_USEC);
                    write(fdIns[0], &(int){1}, sizeof(int));
                    if(select(fdOuts[0] + 1, &fOutSet, NULL, NULL, &tv)) 
                    {
                        read(fdOuts[0], &results[0], sizeof(&results[0]));
                        attempt++;
                        printf("%c(%d): %s (Attempt %d)\n", funcPrefixes[0], x,
                            symbolic_status(results[0].status), attempt);
                        if (results[0].status != COMPFUNC_SOFT_FAIL
                            || attempt >= MAX_ATTEMPTS_COUNT) 
                        {
                            isBreak = true;
                            break;
                        }
                    }
                }
                if (isBreak) break;
                if (res != 'b') 
                {
                    printPrompt();
                    if (res == 'c') return;
                }
            }
            if (results[0].status == COMPFUNC_SOFT_FAIL
                || results[0].status == COMPFUNC_HARD_FAIL) 
            {
                write(fdIns[0], &(int){0}, sizeof(int));
                results[0].status = COMPFUNC_HARD_FAIL;
                printf("%c(%d): hard-fail\n", funcPrefixes[0], x);
                return;
            }
        }
        case COMPFUNC_STATUS_MAX:
        case COMPFUNC_SUCCESS:
        {
            write(fdIns[0], &(int){0}, sizeof(int));
            printFuncResult(funcPrefixes[0], x, results[0]);

            if (!isNested) {
                int attemptsCount = (int)(WITHOUT_PROMPT_TIME /
                    (SELECT_TIMEOUT_SEC + SELECT_TIMEOUT_USEC / (float)1000000));
                while(1) 
                {
                    for(int i = 0; i < attemptsCount; i++) 
                    {
                        fd_set fOutSet = createFdSet((int[]){fdOuts[1]}, 1);
                        timeval_t tv = createTimeval(SELECT_TIMEOUT_SEC, SELECT_TIMEOUT_USEC);
                        if(select(fdOuts[1] + 1, &fOutSet, NULL, NULL, &tv)) 
                        {
                            read(fdOuts[1], &results[1], sizeof(&results[1]));
                            handleResult(x,
                                (char[]){funcPrefixes[1]},
                                &results[1],
                                (int[]){fdIns[1]},
                                (int[]){fdOuts[1]},
                                true);
                            return;
                        }
                    }
                    if (res != 'b') 
                    {
                        printPrompt();
                        if (res == 'c') return;
                    }
                }
            }
            break;
        }
        default:
            printf("%c(%d): hard-fail\n", funcPrefixes[0], x);
            break;
    }
}

void runManager() 
{
    int x;
    printf("Enter x: ");
    scanf("%d", &x);
    write(fIn[1], &x, sizeof(x));
    write(gIn[1], &x, sizeof(x));
    
    result_t fResult, gResult;
    int max = fOut[0] > gOut[0] ? fOut[0] : gOut[0];
    
    int attemptsCount = (int)(WITHOUT_PROMPT_TIME /
        (SELECT_TIMEOUT_SEC + SELECT_TIMEOUT_USEC / (float)1000000));
    while (1) 
    {
        int attempt = 0;
        for(int i = 0; i < attemptsCount; i++) 
        {
            fd_set fOutSet = createFdSet((int[]){fOut[0], gOut[0]}, 2);
            timeval_t tv = createTimeval(SELECT_TIMEOUT_SEC, SELECT_TIMEOUT_USEC);
            if(select(max + 1, &fOutSet, NULL, NULL, &tv)) 
            {
                if (FD_ISSET(fOut[0], &fOutSet)) {
                    read(fOut[0], &fResult, sizeof(&fResult));
                    result_t results[] = {fResult, gResult};
                    handleResult(x,
                        (char[]){'f', 'g'},
                        results,
                        (int[]){fIn[1], gIn[1]},
                        (int[]){fOut[0], gOut[0]},
                        false);
                    fResult = results[0];
                    gResult = results[1];
                    printResult(fResult, gResult);
                    return;
                }
                else 
                {
                    read(gOut[0], &gResult, sizeof(&gResult));
                    result_t results[] = {gResult, fResult};
                    handleResult(x,
                        (char[]){'g', 'f'},
                        results,
                        (int[]){gIn[1], fIn[1]},
                        (int[]){gOut[0], fOut[0]},
                        false);
                    gResult = results[0];
                    fResult = results[1];
                    printResult(fResult, gResult);
                    
                    return;
                }
            }
        }
        if (res != 'b') 
        {
            printPrompt();
            if (res == 'c') return;
        }
    }

}

int main() 
{
    srand(time(NULL));
    pid_t child_a, child_b;

    pipe(fIn);
    pipe(fOut);
    pipe(gIn);
    pipe(gOut);
    
    child_a = fork();
    if (child_a == 0) runProcess(fIn[0], fOut[1], F_FUNC);
    else 
    {
        child_b = fork();
        if (child_b == 0) runProcess(gIn[0], gOut[1], G_FUNC);
        else runManager();
    }
    return 0;
}
