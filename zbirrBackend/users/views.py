from django.shortcuts import render

from rest_framework import serializers
from rest_framework import response
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
import requests

url = "http://localhost:8090/"

@api_view(['GET', 'POST'])
def add_user(request):
    if request.method == 'POST':
        res = requests.post(url + "users/", request.data)
        if res.status_code == 201:
            return Response(request.data, status=status.HTTP_201_CREATED)
        
