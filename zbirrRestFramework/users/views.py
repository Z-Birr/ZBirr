from django.shortcuts import render

from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework import status

from users.models import Balance, Transactions
from users.serializers import BalanceSerializer, TransactionsSerializer, UsersSerializer
from rest_framework.permissions import IsAuthenticated
from django.contrib.auth.models import User
from decimal import Decimal
@api_view(['GET', 'POST'])
def all_users(request):
    if (request.method == 'GET'):
        users = User.objects.all()
        serializer = UsersSerializer(users, many=True)
        return Response(serializer.data)

    elif request.method == 'POST':
        serializer = UsersSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def all_transactions(request):
    if (request.method == 'GET'):
        users = User.objects.all()
        serializer = TransactionsSerializer(users, many=True)
        return Response(serializer.data)

    elif request.method == 'POST':
        serializer = TransactionsSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def balance(request):
    if (request.method == 'GET'):
        users = User.objects.all()
        serializer = TransactionsSerializer(users, many=True)
        return Response(serializer.data)

    elif request.method == 'POST':
        serializer = TransactionsSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def user_detail(request, pk):
    try:
        user = User.objects.get(username=pk)
        if request.method == 'GET':
            serializer = UsersSerializer(user)
        return Response(serializer.data)
    except User.DoesNotExist:
        return Response({"status": "No user found by that id"},status=status.HTTP_404_NOT_FOUND)

    
    # elif request.method == 'PUT':
    #     serializer = UsersSerializer(user, data=request.data)
    #     if serializer.is_valid():
    #         serializer.save()
    #         return Response(serializer.data)
    #     return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    # elif request.method == 'DELETE':
    #     user.delete()
    #     return Response(status=status.HTTP_204_NO_CONTENT)

@api_view(['GET', 'PUT'])
def transaction_detail(request, pk):
    try:
        transaction = Transactions.objects.get(pk=pk)
    except User.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        serializer = UsersSerializer(transaction)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = UsersSerializer(transaction, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'PUT'])
def balance_detail(request, pk):
    try:
        balance = Balance.objects.get(pk=pk)
    except User.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        serializer = UsersSerializer(balance)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = UsersSerializer(balance, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def transfer(request):
    sender = User.objects.get(username=request.user)
    senderBalance = Balance.objects.get(user=sender)
    amount = request.data.get('amount')
    amount = Decimal(amount)
    print(request.data)
    try:
        reciever = User.objects.get(username=request.data.get('uid'))
        print(reciever)
        print("---------1")
        recieverBalance = Balance.objects.get(user=reciever)
        print("---------2")

        if (reciever != sender):
            print("---------3")
            if Decimal(senderBalance.currentBalance) > amount + 5 and amount >= 5:
                print("---------4")

                recieverBalance.currentBalance += amount
                recieverBalance.save()
                senderBalance.currentBalance -= amount
                senderBalance.save()
                transaction = Transactions()
                transaction.amount = amount
                transaction.sender = sender
                transaction.reciever = reciever
                transaction.senderBalance = senderBalance.currentBalance
                transaction.recieverBalance = recieverBalance.currentBalance
                transaction.save()
            else:
                return Response({"status": "current balance or amount is insufficient"})
            return Response({"status": "success"})
        return Response({"status": "sender and reciever must be different"})

    except:
        return Response({"status": "user doesn't exist"}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def initialize(request):
    user = User.objects.get(username=request.user)
    balance = Balance()
    balance.user = user
    balance.currentBalance = 0.0
    balance.save()

    transaction = Transactions()
    transaction.sender = user
    transaction.reciever = user
    transaction.senderBalance = 0.0
    transaction.recieverBalance = 0.0
    transaction.amount = 0.0
    transaction.save()
    return Response({"status": "initialized"})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def transactionTable(request):
    list = []
    user = User.objects.get(username=request.user)
    transactions = Transactions.objects.order_by('-date').filter(reciever=user)
    list.extend(transactions)
    transactions = Transactions.objects.order_by('-date').filter(sender=user)
    list.extend(transactions)
    serializer = TransactionsSerializer(list, many=True)

    return Response({"transactions":serializer.data})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def currentBalance(request):
    user = User.objects.get(username=request.user)
    balance = Balance.objects.get(user=user)
    serializer = BalanceSerializer(balance)
    return Response(serializer.data)