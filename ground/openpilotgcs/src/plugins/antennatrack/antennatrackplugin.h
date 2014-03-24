#ifndef ANTENNATRACKPLUGIN_H
#define ANTENNATRACKPLUGIN_H

#include <extensionsystem/iplugin.h>

class AntennaTrackGadgetFactory;

class AntennaTrackPlugin : public ExtensionSystem::IPlugin {
    Q_OBJECT
                                                   Q_PLUGIN_METADATA(IID "OpenPilot.AntennaTrack")

public:
    AntennaTrackPlugin();
    ~AntennaTrackPlugin();

    void extensionsInitialized();
    bool initialize(const QStringList & arguments, QString *errorString);
    void shutdown();
private:
    AntennaTrackGadgetFactory *mf;
};

#endif // ANTENNATRACKPLUGIN_H
