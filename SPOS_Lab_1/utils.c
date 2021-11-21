#include <utils.h>

fd_set createFdSet(int fds[], int count) 
{
    fd_set fdSet;
    FD_ZERO(&fdSet);
    for (int i = 0; i < count; i++) 
    {
        FD_SET(fds[i], &fdSet);
    }
    return fdSet;
}

timeval_t createTimeval(int sec, int msec) 
{
    struct timeval timeout;
    timeout.tv_sec = sec;
    timeout.tv_usec = msec;
    return timeout;
}

compfunc_status_t f_func(int x, int* res) {
    *(res) = x * x;
    sleep(25);
    return COMPFUNC_SUCCESS;
}

compfunc_status_t g_func(int x, int* res) {
    *(res) = x + x;
    sleep(3);
    int r = rand() % 3;
    if (r == 0) {
        return COMPFUNC_SUCCESS;
    }
    return COMPFUNC_SOFT_FAIL;
}
