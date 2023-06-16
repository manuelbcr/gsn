from django.urls import re_path, include
from . import views
from django.views.decorators.csrf import csrf_exempt
from django.contrib import admin

urlpatterns = [
    re_path(r'^$', views.index, name='index'),
    re_path(r'^sensors/$', views.sensors, name='sensors'),
    re_path(r'^sensors/(?P<sensor_name>(\w)+)/(?P<from_date>(\w|:|-)+)/(?P<to_date>(\w|:|-)+)/$', views.sensor_detail,
        name='sensor_detail'),
    re_path(r'^download/(?P<sensor_name>(\w)+)/(?P<from_date>(\w|:|-)+)/(?P<to_date>(\w|:|-)+)/$', views.download_csv,
        name='download_csv'),
    re_path(r'^download/$', csrf_exempt(views.download), name='download'),
    re_path(r'^profile/$', views.profile, name='profile'),
    re_path(r'^logout/$', views.logout_view, name='logout'),
    re_path(r'^admin/', admin.site.urls),
    re_path(r'^oauth_code/$', views.oauth_get_code, name='oauth_logging_redirect'),
    re_path(r'^favorites/$', views.favorites_manage, name='favorites'),
    re_path(r'^favorites_list/$', views.favorites_list, name='favorites_list'),
    re_path(r'^dashboard/(?P<sensor_name>(\w)+)/$', views.dashboard, name='dashboard'),

    # url(r'^logged/$', views.oauth_after_log, name='oauth_after_log'),
    re_path(r'^accounts/', include('allaccess.urls')),
]
