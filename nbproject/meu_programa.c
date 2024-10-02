gcc -o meu_programa meu_programa.c
./meu_programa

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Definição da estrutura do nó da lista
struct Node {
    int data;
    struct Node* next;
};

// Função para criar um novo nó
struct Node* createNode(int value) {
    struct Node* newNode = (struct Node*)malloc(sizeof(struct Node));
    if (newNode == NULL) {
        printf("Erro ao alocar memória para o novo nó.\n");
        exit(1);
    }
    newNode->data = value;
    newNode->next = NULL;
    return newNode;
}

// Função para adicionar um nó no início da lista
void insertAtBeginning(struct Node** head, int value) {
    struct Node* newNode = createNode(value);
    newNode->next = *head;
    *head = newNode;
}

// Função para adicionar um nó no final da lista
void insertAtEnd(struct Node** head, int value) {
    struct Node* newNode = createNode(value);
    if (*head == NULL) {
        *head = newNode;
        return;
    }
    struct Node* current = *head;
    while (current->next != NULL) {
        current = current->next;
    }
    current->next = newNode;
}

// Função para excluir um nó com valor específico
void deleteNode(struct Node** head, int value) {
    struct Node* temp = *head;
    struct Node* prev = NULL;
    if (temp != NULL && temp->data == value) {
        *head = temp->next;
        free(temp);
        return;
    }
    while (temp != NULL && temp->data != value) {
        prev = temp;
        temp = temp->next;
    }
    if (temp == NULL) {
        printf("Valor não encontrado na lista.\n");
        return;
    }
    prev->next = temp->next;
    free(temp);
}

// Função para localizar um nó com valor específico
void searchNode(struct Node* head, int value) {
    struct Node* current = head;
    while (current != NULL) {
        if (current->data == value) {
            printf("Valor %d encontrado na lista.\n", value);
            return;
        }
        current = current->next;
    }
    printf("Valor %d não encontrado na lista.\n", value);
}

// Função para exibir a lista
void displayList(struct Node* head) {
    struct Node* current = head;
    while (current != NULL) {
        printf("%d ", current->data);
        current = current->next;
    }
    printf("\n");
}

int main() {
    struct Node* myList = NULL;

    insertAtBeginning(&myList, 10);
    insertAtBeginning(&myList, 20);
    insertAtEnd(&myList, 30);
    insertAtEnd(&myList, 40);

    printf("Lista original: ");
    displayList(myList);

    deleteNode(&myList, 20);
    printf("Lista após excluir o valor 20: ");
    displayList(myList);

    searchNode(myList, 30);

    return 0;
}
