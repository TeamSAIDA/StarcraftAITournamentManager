#pragma once
#include <BWAPI.h>
#include <vector>
#include <windows.h>
#include <Shlwapi.h>
#include <iostream>
#include <fstream>
#include "Timer.h"
#include <iostream>
#include <cstdlib>
#include <sstream>
#include "AutoObserver.h"

#define MINIMUM_COMMAND_OPTIMIZATION 1

class ExampleTournamentModule : public BWAPI::TournamentModule
{
  virtual bool onAction(int actionType, void *parameter = nullptr);
  virtual void onFirstAdvertisement();
};

class ExampleTournamentAI : public BWAPI::AIModule
{
    AutoObserver _autoObserver;

public:

  virtual void onUnitComplete(BWAPI::Unit *unit);
  virtual void onPlayerDropped(BWAPI::Player* player);
  virtual void drawUnitInformation(int x, int y);
  virtual void drawTournamentModuleSettings(int x, int y);
  virtual void parseConfigFile(const std::string & filename);
  virtual std::vector<std::string> getLines(const std::string & filename);

    virtual void onStart();
  virtual void onEnd(bool isWinner);
  virtual void onFrame();
  virtual void onSendText(std::string text);
  virtual void onReceiveText(BWAPI::Player player, std::string text);
  virtual void onPlayerLeft(BWAPI::Player player);
  virtual void onNukeDetect(BWAPI::Position target);
  virtual void onUnitDiscover(BWAPI::Unit unit);
  virtual void onUnitEvade(BWAPI::Unit unit);
  virtual void onUnitShow(BWAPI::Unit unit);
  virtual void onUnitHide(BWAPI::Unit unit);
  virtual void onUnitCreate(BWAPI::Unit unit);
  virtual void onUnitDestroy(BWAPI::Unit unit);
  virtual void onUnitMorph(BWAPI::Unit unit);
  virtual void onUnitRenegade(BWAPI::Unit unit);
  virtual void onSaveGame(std::string gameName);

};
