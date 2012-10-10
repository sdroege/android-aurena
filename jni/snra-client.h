#ifndef __SNRA_CLIENT_H__
#define __SNRA_CLIENT_H__

#include <gst/gst.h>
#include <gst/net/gstnet.h>
#include <libsoup/soup.h>
#include <json-glib/json-glib.h>

#include "snra-types.h"

G_BEGIN_DECLS

#define SNRA_TYPE_CLIENT (snra_client_get_type ())

typedef struct _SnraClientClass SnraClientClass;

struct _SnraClient
{
  GObject parent;

  GstState state;
  gboolean enabled;
  gboolean paused;

  GstClock *net_clock;
  gchar *server_host;
  gint server_port;

  SoupSession *soup;
  JsonParser *json;

  GMainContext * context;

  GstElement *player;

  guint timeout;

  gboolean connecting;
  gboolean was_connected;
  gchar *connected_server;
  gint connected_port;
};

struct _SnraClientClass
{
  GObjectClass parent;
};

GType snra_client_get_type(void);
SnraClient *snra_client_new(GMainContext * context, const gchar *server);

G_END_DECLS
#endif
