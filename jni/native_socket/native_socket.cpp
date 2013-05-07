
#include <android/log.h>
#define LOG_TAG "MYAMA-NATIVE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#include <errno.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/socket.h>
#include <unistd.h>

static const int PORT_NUMBER = 6969;

int
main(int argc, char **argv)
{
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        printf("Failed to create socket. errno(%d)\n", errno);
        return 1;
    }
    printf("Socket created.\n");
    printf("\n");

    // Ask port number
    //printf("Please input the port number: [6969]\n");
    //getnstr();
    //printf("\n");

    int port = PORT_NUMBER;
    if (true) {
        port = 69;
    }

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;

    printf("CALL bind() socket port(%d).\n", port);
    int ret = bind(sock, (struct sockaddr *)&addr, sizeof(addr));
    if (ret < 0) {
        printf("Failed to bind socket. errno(%d)\n", errno);
    } else {
        printf("Bind socket.\n");
    }

    ret = listen(sock, 1);
    if (ret < 0) {
        printf("Failed to listen socket. errno(%d)\n", errno);
    } else {
        printf("Listen socket.\n");
    }

    printf("Please hit any key ...\n");
    getchar();

    close(sock);
    return 0;
}
