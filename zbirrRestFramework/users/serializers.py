from django.db.models import fields
from django.db.models.base import Model
from rest_framework import serializers
from users.models import Balance, Transactions
from django.contrib.auth.models import User

class UsersSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['first_name', 'last_name']

class TransactionsSerializer(serializers.ModelSerializer):
    sender = UsersSerializer(read_only=True)
    reciever = UsersSerializer(read_only=True)
    class Meta:
        model = Transactions
        fields = [ 'sender', 'reciever', 'date','amount']

class BalanceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Balance
        fields = ['currentBalance']