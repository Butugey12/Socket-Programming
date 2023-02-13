#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int func();
int main() {
	printf("func: %d", func());
	
	return 0;
}
int func() {
	return 1234;
}