from django.conf.urls import url
from users import views

urlpatterns = [
    url(r'', views.add_user),
    # url(r'^user/(?P<pk>[a-zA-Z0-9]+)/$', views.user_detail),
    # url(r'^transactions/$', views.all_transactions),
    # url(r'^transaction/(?P<pk>[0-9]+)', views.transaction_detail),
    # url(r'^balance/$', views.balance),
    # url(r'^balance/(?P<pk>[a-zA-Z0-9]+)', views.balance_detail),
    
]