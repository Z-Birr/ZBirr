from django.db.models import fields
from django.db.models.base import Model
from rest_framework import serializers
from users.models import Balance, Transactions
from django.contrib.auth.models import User

class UsersSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'first_name', 'last_Name', 'password']

class TransactionsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Transactions
        fields = ['user', 'sender', 'reciever', 'date']

class BalanceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Balance
        fields = ['user', 'currentBalance']