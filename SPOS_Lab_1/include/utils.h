#ifndef _OS_LAB1_UTIL_H
#define _OS_LAB1_UTIL_H
#ifdef _MSC_VER
# include "util_exports.h"
#else
# define LAB1_EXPORTS
#endif
#include <sys/types.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>

#include "compfuncs.h"

struct _result {
    compfunc_status_t status;
    int value;
};

typedef struct _result result_t;
typedef struct timeval timeval_t;

fd_set createFdSet(int fds[], int count);
timeval_t createTimeval(int sec, int msec);

compfunc_status_t f_func(int x, int* res);
compfunc_status_t g_func(int x, int* res);
#endif // _OS_LAB1_UTIL_H
