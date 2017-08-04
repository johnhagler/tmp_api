/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.dsp.common.GenericDSP;
import mvx.dsp.obj.*;
import mvx.util.*;
import mvx.db.common.GenericDef;
import mvx.db.common.Expression;
import mvx.db.common.FieldSelection;

/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Wrk: Supplier invoice batch</FONT></B><BR><BR>
*
* This interactive program manages Invoice batch numbers by calling function program APS450Fnc.
* APS450 is to be used as a template for master data programs using Fnc-type
* funtion programs.
*<PRE>
*<B>Use of reserved indicators in program (all panels)</B>
*41-49      Used for option 1 - 9
*50-51      Subfile handling
*60         General error handling
*91-93      Chain/Lock/Read
*94-96      Subfile handling 
*</PRE>
*<B>Use of indicators on B-panel</B>
*01-30      Used for protect field on subfile line 
*31-39      Free to use
*53-59      Free to use
*52         Indicates that central user is running the program
*61         Reserved for enhancements in standard 
*62         Reserved for enhancements in standard
*63         Reserved for enhancements in standard
*64         Display F23 + button for aggregation drill-up
*65         Display F16 Run Report
*66         Reverse image on column 
*67         Display selection 2 from-to fields - on B-panel
*68         Display selection 3 from-to fields - on B-panel
*69         Display drop down for number of filters - on B-panel
*70         Display selection 1 from-to fields - on B-panel
*71         Display positioning field for keyfield 1 - on B-panel
*72         Display positioning field for keyfield 2 - on B-panel
*73         Display positioning field for keyfield 3 - on B-panel
*74         Display positioning field for keyfield 4 - on B-panel
*75         Display positioning field for keyfield 5 - on B-panel
*76         Display positioning field for keyfield 6 - on B-panel
*77         Display positioning field for keyfield 7 - on B-panel
*78         Display positioning field for keyfield 8 - on B-panel
*79         Protect filter field 1 if single division - on B-panel
*80         Display drop down for aggregation - on B-panel
*81         Display heading for positioning field for keyfield 1 - on B-panel
*82         Display heading for positioning field for keyfield 2 - on B-panel
*83         Display heading for positioning field for keyfield 3 - on B-panel
*84         Display heading for positioning field for keyfield 4 - on B-panel
*85         Display heading for positioning field for keyfield 5 - on B-panel
*86         Display heading for positioning field for keyfield 6 - on B-panel
*87         Display heading for positioning field for keyfield 7 - on B-panel
*88         Display heading for positioning field for keyfield 8 - on B-panel
*89         Display heading for positioning field for keyfield 9-12 - on B-panel
*/
public class APS450 extends Interactive
{
   /**
   * B-panel - initiate.
   */
   public void PBINZ() {
      ensureReadOfTables();
      // Forget notifications
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      // Set last panel
      lastPanel = 'B';
      // Set display subfile as default
      displaySubfile = true;
      // Set not to force initiation of subfile as default
      forceInitOfSubfile = false;
      // Set not to force initiation of subfile as default
      forceInitOfSubfile = false;
      // Position cursor
      if (!isBookmarkProcessing()) {
         DSP.setFocus("W1OBKV");
      }
      // Init new Sorting order 	 	
      if (!isBookmarkProcessing()) {
         if (!initNewSortingOrder()) {
            displaySubfile = false;
         }
         // - Init new View
         if (CRMNGVW.initView(DSP, DSP.WWQTTP, SYSPV, DSP.WOPAVR, "WOPAVR", DSP.WOUPVR, this.DSUSS)) { 	 	 	 	
            if (CRMNGVW.PXIN60) { 	 	 	 	
               displaySubfile = false;
               return; 	 	 	
            } 	 	 	
            prepareListHead();
         }
         // - Init new Filter
         if (DSP.WWNFTR != XBNFTR) {
            if (!CRMNGVW.checkFilterValue(DSP, "WWNFTR", DSP.WWNFTR, SYSOR, singleDivision)) {
               displaySubfile = false;
            }
            prepareListHead();
            XBAGGR = DSP.WWAGGR;
         }
         // - Init new Aggregation
         if (DSP.WWAGGR != XBAGGR) {    	
            prepareListHead();
         }
      }
      // Check authority for division
      if (!CRMNGVW.checkDivisionAuthority(DSP, SYSOR, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, 
         DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, "W1OBKV", "W2OBKV", 
         "W3OBKV", "W4OBKV", "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV", SYVIU.getSLF1(), 
         SYVIU.getSLF2(), SYVIU.getSLF3(), DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, 
         DSP.WFSLC3, DSP.WTSLC3, DSP.WWNFTR)) {
         displaySubfile = false;
      }
      // Check Listhead fields
      if (!CRMNGVW.checkListHead(DSP, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, 
            DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, 
            DSP.WFSLC3, DSP.WTSLC3, "W1OBKV", "W2OBKV", "W3OBKV", "W4OBKV", "W5OBKV", "W6OBKV", 
            "W7OBKV", "W8OBKV", "WFSLCT", "WTSLCT", "WFSLC2", "WTSLC2", "WFSLC3", "WTSLC3")) {
         displaySubfile = false;
      }
      // Avoid display of first subfile
      if (!initSubfile) {
         displaySubfile = false;
         // Make sure to force initiation of subfile if user press Next
         forceInitOfSubfile = true;
         IN60 = true;
         // MSGID=XENF501 *NODSP mode is choosen, press Next/Refresh to display values
         COMPMQ("XENF501");
      }
      // Init of subfile information
      XXPAVR.move(DSP.WOPAVR);
      XXUPVR.move(DSP.WOUPVR);
      XBNFTR = DSP.WWNFTR;
      XBAGGR = DSP.WWAGGR;
      XFSLCT.moveLeftPad(DSP.WFSLCT);
      XTSLCT.moveLeftPad(DSP.WTSLCT);
      XFSLC2.moveLeftPad(DSP.WFSLC2);
      XTSLC2.moveLeftPad(DSP.WTSLC2);
      XFSLC3.moveLeftPad(DSP.WFSLC3);
      XTSLC3.moveLeftPad(DSP.WTSLC3);
      X1OBKV.move(DSP.W1OBKV);
      X2OBKV.move(DSP.W2OBKV);
      X3OBKV.move(DSP.W3OBKV);
      X4OBKV.move(DSP.W4OBKV);
      X5OBKV.move(DSP.W5OBKV);
      X6OBKV.move(DSP.W6OBKV);
      X7OBKV.move(DSP.W7OBKV);
      X8OBKV.move(DSP.W8OBKV);
      X9OBKV.move(DSP.W9OBKV);
      XAOBKV.move(DSP.WAOBKV);
      XBOBKV.move(DSP.WBOBKV);
      XCOBKV.move(DSP.WCOBKV);
      // Set table fields based on filter and position fields
      if (displaySubfile) {
         CRMNGVW.setTableFromListHead(DSP, SYIBC, SYSOR, SYVIU, APIBH, DSP.W1OBKV, DSP.W2OBKV, 
               DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, 
               DSP.W9OBKV, DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV);
      }
      this.WSRGTM = movexDateTime();
      DSP.XBRRNA = 0;
      DSP.XBRRNM = 0;
      // Do not fill the subfile at this stage
      if (isBookmarkProcessing()) {
         return;
      }
      // Clear the subfile
      IN94 = true;
      IN95 = false;
      DSP.clearSFL("BC");
      IN94 = false;
      IN95 = false;
      IN96 = false;
      initSubfile = true;
      // Set limit & build subfile
      if (displaySubfile) {
         if (IN70 || IN67 || IN68) {
            mainTableSelection = CRMNGVW.setSelection(mainTableSelection, SYIBC.getFILE(), SYVIU.getSLF1(), SYVIU.getSLF2(), SYVIU.getSLF3(), SYSOR);
         }
         PXAOPT.move("SETLL");
         PXAKNO = CRMNGVW.getNumberOfKeyFields();
         accessTable();
         PBINZ_SETLL();
         PBROL();
      }
   }

   /**
   * B-panel - display - initiate.
   */
   public void PBDSP_INZ() {
      // Set last panel
      lastPanel = 'B';
      // Restore panel sequence after setSpecialPanelSequence
      CRCommon.restorePanelSequence(saveSEQ);
      // Indicate central user
      IN52 = CRCommon.isCentralUser();
   }

   /**
   * B-panel - display - get next Standard view.
   */
   public void PBDSP_F10() { 	
      CRMNGVW.nextStandardView(this, DSP.WOPAVR, DSP.WOUPVR, SYSPV, 'B'); 	 	
      return; 	
   }

   /**
   * Delete selection
   */    
   public void PBDSP_F14() {
      picSetMethod('D');
      // Call APS455 Supplier invoice batch. Select operation
      if (!callAPS455(DSP.WFDIVI, /*INBN*/ 0, cRefIBOPext.DELETE())) {
         picSetMethod('D');
         IN60 = true;
      }
   }

  /**
   *    PBDSP_F16 - Run report, Create XML lines
   */
   public void PBDSP_F16() {
      if (IN65) {
         //   Call AHS150 for submit of print
         CRMNGVW.runReport(SYIBC, SYVIU, DSP.WOPAVR, DSP.WOUPVR, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, 
               DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, DSP.W9OBKV, DSP.WAOBKV, 
               DSP.WBOBKV, DSP.WCOBKV, DSP.WWNFTR, DSP.WWQTTP, DSP.WWAGGR);
      }
      picSetMethod('D');
      return;
   }

   /**
   * Print selection
   */    
   public void PBDSP_F17() {
      picSetMethod('D');
      // Call APS455 Supplier invoice batch. Select operation
      if (!callAPS455(DSP.WFDIVI, /*INBN*/ 0, cRefIBOPext.PRINT())) {
         picSetMethod('D');
         IN60 = true;
      }
   }

   /**
   * Validation selection
   */    
   public void PBDSP_F18() {
      picSetMethod('D');
      // Call APS455 Supplier invoice batch. Select operation
      if (!callAPS455(DSP.WFDIVI, /*INBN*/ 0, cRefIBOPext.VALIDATE())) {
         picSetMethod('D');
         IN60 = true;
      }
   }

   /**
   * Update to APL selection
   */    
   public void PBDSP_F19() {
      picSetMethod('I');
      // Call APS455 Supplier invoice batch. Select operation
      if (!callAPS455(DSP.WFDIVI, /*INBN*/ 0, cRefIBOPext.UPDATE_TO_APL())) {
         picSetMethod('D');
         IN60 = true;
      }

   }

  /**
   * Aggregation drill-up
   */    
   public void PBDSP_F23() {
      if (CRMNGVW.drillUp(this, DSP.WWNFTR, DSP.WWAGGR, DSP.W1OBKV, 
                  DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, 
                  DSP.W7OBKV, DSP.W8OBKV, DSP.W9OBKV, DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV)) {
         DSP.WWNFTR--;
      }
      return;
   }

   /**
   * B-panel - check.
   */
   public void PBCHK() {
      ensureReadOfTables();
      DSP.WBRRNA = 0;
      PBCHK_process();
      PBCHK_ExecuteOption(); // exit point for modification
      // Clear option in case of error
      if (DSP.WBRRNA != 0 && IN60) {
         DSP.clearOption();
         DSP.XBRRNA = DSP.WBRRNA;
         if (DSP.S0NAGG > 1) {
            // Reverse image on aggregated line
            IN66 = true;
         }
         DSP.updateSFL("BS");
         IN66 = false;
      }
   }

   /**
   * B-panel - check - validate and process user action.
   *
   * Turns on IN60 if an error was found.
   */
   public void PBCHK_process() {
      // Clear list update indicator
      boolean listUpdate = false;
      // Check if View created/updated by UI. In that case a refresh is needed 	 	
      CRMNGVW.checkUIAction(DSP, refreshReason, XXPAVR, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR,  SYSPV, SYSPP, 'B', DSP.WWQTTP);
      // Check sorting order
      if (PBCHK_ValidateInquiry()) {  // exit point for modification
         if (IN60) {
            return;
         }
      }
      CRMNGVW.checkSorting(DSP.WWQTTP, SYVIU); 	 	
      if (CRMNGVW.PXIN60) { 	 	
         picSetMethod('D'); 	 	
         IN60 = true; 	 	
         DSP.setFocus("WWQTTP"); 	 	
         return; 	 	 	 	
      }
      // New sorting order
      if (DSP.WWQTTP != XXQTTP) {
         picSetMethod('I');
         return;
      }
      // Check authority for division
      if (!CRMNGVW.checkDivisionAuthority(DSP, SYSOR, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, 
         DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, "W1OBKV", "W2OBKV", 
         "W3OBKV", "W4OBKV", "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV", SYVIU.getSLF1(), 
         SYVIU.getSLF2(), SYVIU.getSLF3(), DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, 
         DSP.WFSLC3, DSP.WTSLC3, DSP.WWNFTR)) {
         picSetMethod('D');
         return;
      }
      // Check View 	 	
      if (DSP.WOPAVR.NE(XXPAVR) || 	 	 	
          DSP.WOUPVR.NE(XXUPVR) || 	 	
          (DSP.WOPAVR.isBlank() && DSP.WOUPVR.isBlank())) { 	 	 	
         CRMNGVW.PGNM.move(this.DSPGM); 	 	 	 	
         CRMNGVW.checkView(DSP, DSP.WWQTTP, SYSPV, DSP.WOPAVR, DSP.WOUPVR, "WOPAVR");
         if (CRMNGVW.PXIN60) { 	 	 	 	
            picSetMethod('D'); 	 	
            return; 	 	
         } 	 	
         picSetMethod('I'); 	 	
         return; 	 	 	 	
      }
      // Check Filter
      if (!CRMNGVW.checkFilterValue(DSP, "WWNFTR", DSP.WWNFTR, SYSOR, singleDivision)) {
         return;
      }
      // Check Listhead fields
      if (!CRMNGVW.checkListHead(DSP, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, 
            DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, 
            DSP.WFSLC3, DSP.WTSLC3, "W1OBKV", "W2OBKV", "W3OBKV", "W4OBKV", "W5OBKV", "W6OBKV", 
            "W7OBKV", "W8OBKV", "WFSLCT", "WTSLCT", "WFSLC2", "WTSLC2", "WFSLC3", "WTSLC3")) {
         picSetMethod('D');
         IN60 = true;
         return;
      }
      // Check panel sequence
      if (!seqCheckSeq(getAllowedPanels(), DSP.WWPSEQ)) {
         return;
      }
      this.CSSQ.moveLeft(this.SEQ);
      // Get option and set primary keys
      if (isBookmarkProcessing()) {
         setKeyFieldsInPBCHKFromBookmark();
      } else {
         // Option and keys from subfile head
         DSP.WSOPT2.clear();
         DSP.WSOPT2.move(DSP.WBOPT2);
         APIBH.setINBN(0L);
         APIBH.setDIVI().clear();
         // - Set table fields based on filter and position fields
         CRMNGVW.setTableFromListHead(DSP, SYIBC, SYSOR, SYVIU, APIBH, DSP.W1OBKV, 
               DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, 
               DSP.W7OBKV, DSP.W8OBKV, DSP.W9OBKV, DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV);
      }
      // - option and keys from subfile
      if (DSP.WSOPT2.isBlank() && DSP.XBRRNM != 0) {
         boolean endOfSfl = false;
         do {
            DSP.WBRRNA = 0;
            endOfSfl = DSP.readSFL("BS");
            if (!endOfSfl) {
               APIBH.setDIVI().moveLeftPad(DSP.S0DIVI);
               APIBH.setINBN(DSP.S0INBN);
               if (toBoolean(DSP.WSCHG) && DSP.WSOPT2.NE("2 ") && DSP.WSOPT2.NE(" 2") ) {
                  // Field changed in subfile line
                  listUpdate = true;
               }
            }
         } while (DSP.WSOPT2.isBlank() && !endOfSfl && !listUpdate);
      }
      if (pMaintain != null && !listUpdate) {
         pMaintain.messages.forgetNotifications();
      }     
      // New start position
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         if (DSP.W1OBKV.NE(X1OBKV) ||
             DSP.W2OBKV.NE(X2OBKV) ||
             DSP.W3OBKV.NE(X3OBKV) ||
             DSP.W4OBKV.NE(X4OBKV) ||
             DSP.W5OBKV.NE(X5OBKV) ||
             DSP.W6OBKV.NE(X6OBKV) ||
             DSP.W7OBKV.NE(X7OBKV) ||
             DSP.W8OBKV.NE(X8OBKV) ||
             DSP.W9OBKV.NE(X9OBKV) ||
             DSP.WAOBKV.NE(XAOBKV) ||
             DSP.WBOBKV.NE(XBOBKV) ||
             DSP.WCOBKV.NE(XCOBKV) ||
             DSP.WFSLCT.NE(XFSLCT) ||
             DSP.WTSLCT.NE(XTSLCT) ||
             DSP.WFSLC2.NE(XFSLC2) ||
             DSP.WTSLC2.NE(XTSLC2) ||
             DSP.WFSLC3.NE(XFSLC3) ||
             DSP.WTSLC3.NE(XTSLC3) ||
             DSP.WWAGGR != XBAGGR ||
             DSP.WWNFTR != XBNFTR ||
             forceInitOfSubfile) {
            picSetMethod('I');
            return;
         }
         PBCHK_CheckStartPosition(); // exit point for modification
      }
      // No option given
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         picSetMethod('D');
         return;
      }
      // Check option - adjust option and check if authorized
      if (listUpdate) {
         this.XXOPT2.move(" 2");
         if (PxCHKoption()) {
            return;
         }
      }
      XXOPT2.clear();
      if (!DSP.WSOPT2.isBlank()) {
         this.XXOPT2.move(DSP.WSOPT2);
         if (PxCHKoption()) {
            return;
         }
      }
      // Option not allowed for aggregated lines
      if (DSP.S0NAGG != 1 &&
         (!DSP.WSOPT2.isBlank() || listUpdate) &&
          !isBookmarkProcessing() &&
          XXOPT2.NE(" 1") &&
          XXOPT2.NE("10")) {
         picSetMethod('D');
         IN60 = true;
         // MSGID=XOP0007 Option &1 is not allowed for a aggregated line
         COMPMQ("XOP0007", formatToString(DSP.WSOPT2));
         return;
      }
      // Reset picture mode indicators IN41 - IN49
      CRCommon.clearMode();
      // Set panel sequence
      seqLoadSavedSeq();
      // Transfer data to calling program
      if (this.XXOPT2.EQ(" 1") && DSP.WBRRNA > 0 && cCRCommon.checkIfTransferData(this)) {
         Bookmark bookmark = getBookmark();
         bookmark.setRecord("FAPIBH", getPrimaryKeyForTable("FAPIBH"));
         bookmark = null;
         LDAZZ.TPGM.move(this.DSPGM);
         picPush("**");
         return;
      }
      // Check if valid option
      if (!DSP.WSOPT2.isBlank() && !PBCHK_ValidateOption()) { // exit point for modification
         if ((this.XXOPT2.LT(" 1")  ||
             this.XXOPT2.GT(" 6")) &&
             this.XXOPT2.NE(" 7") &&
             this.XXOPT2.NE(" 8") &&
             this.XXOPT2.NE(" 9") &&
             this.XXOPT2.NE("10") &&
             this.XXOPT2.NE("11") &&
             this.XXOPT2.NE("12") &&
             this.XXOPT2.NE("13") &&
             this.XXOPT2.NE("14") &&
             this.XXOPT2.NE("20") &&
             this.XXOPT2.NE("21") &&
             this.XXOPT2.NE("22") &&
             this.XXOPT2.NE("23") &&
             this.XXOPT2.NE("24"))
         {
            picSetMethod('D');
            IN60 = true;
            if (DSP.WBRRNA == 0) {
               DSP.setFocus("WBOPT2");
            }
            // MSGID=WOP0101 Option &1 is invalid
            COMPMQ("WOP0101", this.XXOPT2);
            return;
         }
      }
      // Check if option (1-9) is authorized
      this.XI = toInt(this.XXOP22.getChar());
      if (this.XI >= 1 && this.XI <= 9 && this.XXOP21.getChar() == ' ' && !toBoolean(this.PXO.charAt(this.XI - 1)) ||
         listUpdate && !toBoolean(this.PXO.charAt(2 - 1)) )
      {
         picSetMethod('D');
         IN60 = true;
         if (DSP.WBRRNA == 0) {
            DSP.setFocus("WBOPT2");
         }
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         // MSGID=XAU0001 You do not have authorization for the selected option
         COMPMQ("XAU0001");
         return;
      }
      // Check key values
      if (this.XXOPT2.EQ(" 1")) {
         APIBH.setINBN(0L);
      } else {
         if (APIBH.getINBN() == 0L) {
            picSetMethod('D');
            IN60 = true;
            CRMNGVW.setFocusOnFilterField(DSP, SYSOR, "E5INBN", "W1OBKV", "W2OBKV", "W3OBKV", "W4OBKV", 
               "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV", "W9OBKV", "WAOBKV", "WBOBKV", "WCOBKV");
            // MSGID=WINBN02 Invoice batch number must be entered
            COMPMQ("WINBN02");
            return;
         }
      }
      // Save indicators 31-40 and 61-90
      moveFromIN(savedIndicators31to40, /*fromInd*/ 31);
      moveFromIN(savedIndicators61to90, /*fromInd*/ 61);
      // Check record
      found_FAPIBH = APIBH.CHAIN("00", APIBH.getKey("00"));
      passFAPIBH = found_FAPIBH;
      IN91 = !found_FAPIBH;
      if (isBookmarkProcessing()) { 	
         validateRecordInPBCHKInCaseOfBookmark(); 	
         if (IN60) { 	
            return;
         } 	
      } 	
      // Add
      if (seqAddModeOk(getPanelsRequiredForAdd())) {
         if (CRCommon.getMode() == cEnumMode.ADD) {
            CRCommon.addPanelsFromSelectedPanelSequence();
            CRCommon.removePanelFromPanelSequence('3');  //Panel 3 cannot be used for all IBTP
            return;
         }
      } else {
         if (DSP.WBRRNA == 0) {
            CRMNGVW.setFocusOnFilterField(DSP, SYSOR, "E5INBN", "W1OBKV", "W2OBKV", "W3OBKV", "W4OBKV", 
               "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV", "W9OBKV", "WAOBKV", "WBOBKV", "WCOBKV");
         }
         // MSGID=WINBN04 Invoice batch number &1 already exists
         COMPMQ("WINBN04", formatToString(APIBH.getINBN(), cRefINBN.length()));
         return;
      }
      // Check if exist
      if (!found_FAPIBH) {
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         picSetMethod('D');
         IN60 = true;
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         COMPMQ("WINBN03", formatToString(APIBH.getINBN(), cRefINBN.length()));
         return;
      }
      // Handle update in list
      if (listUpdate) {
         CRCommon.setChangeMode();
         if (updateFromSflB()) {
            pMaintain.messages.forgetNotifications();
            // Prepare to check next subfile line
            picSetMethod('C');
            // Update subfile
            lastPanel = 'B'; // Set last panel
            setFocus_on_subfile_B(DSP.WBRRNA);
            moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
            DSP.clearOption();
            DSP.XBRRNA = DSP.WBRRNA;
            PBMSF();
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
            if (DSP.S0NAGG > 1) {
               // Reverse image on aggregated line
               IN66 = true;
            }
            DSP.updateSFL("BS");
            IN66 = false;
            CRCommon.clearMode();
         } else {
            IN60 = true; 	
            picSetMethod('D');  // Display message
            return;
         }
      }
      // Remove panel from sequence if not valid
      if (XXOPT2.EQ(" 2") ||
          XXOPT2.EQ(" 3") ||
          XXOPT2.EQ(" 5"))
      {
         if (APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
             APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) 
         {
            CRCommon.removePanelFromPanelSequence('3');
         }
      }
      // Change / Copy / Delete / Display
      if (!seqChangeModeOk() ||
          !seqCopyModeOk() ||
          !seqDeleteModeOk() ||
          !seqDisplayModeOk()) 
      {
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         COMPMQ("WINBN03", formatToString(APIBH.getINBN(), cRefINBN.length()));
         return;
      }
      // Don't display or change if no panels in panel sequence
      if (XXOPT2.EQ(" 2") || XXOPT2.EQ(" 5")) {
         if (picGetPanel() == ' ' || picGetPanel() == '-') {
            picPop();
         }
      }
      // Print
      if (XXOPT2.EQ(" 6")) {
         if (!APS450Fnc.statusOKForPrint(APIBH)) {
            picSetMethod('D');
            IN60 = true;
            // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
            COMPMQ("X_00051", formatToString(SRCOMRCM.getMessage("X__0435", "MVXCON")), formatToString(XXOPT2), formatToString(APIBH.getSUPA(), 15));
            return;
         }
         picSetMethod('U');
         // Call APS455 Supplier invoice batch. Select operation
         if (!callAPS455(APIBH.getDIVI(), APIBH.getINBN(), cRefIBOPext.PRINT())) {
            picSetMethod('D');
            IN60 = true;
         }
         return;
      }
      // Change division
      if (XXOPT2.EQ(" 7")) {
         picSetMethod('U');
         CRCommon.setSpecialPanelSequence("K", saveSEQ);
         picPush(this.SEQ.charAt(0), 'I');
         return;
      }
      // Validation
      if (XXOPT2.EQ(" 8")) {
         if (!APS450Fnc.statusOKForValidate(APIBH)) {
            picSetMethod('D');
            IN60 = true;
            // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
            COMPMQ("X_00051", formatToString(SRCOMRCM.getMessage("X__6536", "MVXCON")), formatToString(XXOPT2), formatToString(APIBH.getSUPA(), 15));
            return;
         }
         picSetMethod('U');
         // Call APS455 Supplier invoice batch. Select operation
         if (!callAPS455(APIBH.getDIVI(), APIBH.getINBN(), cRefIBOPext.VALIDATE())) {
            picSetMethod('D');
            IN60 = true;
         }
         return;
      }
      // Update to APL
      if (XXOPT2.EQ(" 9")) {
         if (APIBH.getIBHE() == cRefIBHEext.ERRORS() ||
             APIBH.getIBLE() == cRefIBLEext.ERRORS()) {
            picSetMethod('D');
            IN60 = true;
            // MSGID AP05003 Update is only permitted if there is no error status
            COMPMQ("WINBN03", formatToString(APIBH.getINBN(), cRefINBN.length()));
            return;
         }
         if (!APS450Fnc.statusOKForUpdAPL(APIBH)) {
            picSetMethod('D');
            IN60 = true;
            // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
            COMPMQ("X_00051", formatToString(SRCOMRCM.getMessage("X__4897", "MVXCON")), formatToString(XXOPT2), formatToString(APIBH.getSUPA(), 15));
            return;
         }
         // Check that all lines putaway - if selfbilling, manual call in APS455, and AUT2=2
         if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING())) {
            boolean allRequiredLinesPutAway = true;
            found_MPAGRS = false;
            APIBL.setCONO(APIBH.getCONO());
            APIBL.setDIVI().move(APIBH.getDIVI());
            APIBL.setINBN(APIBH.getINBN());
            APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
            APIBL.SETLL("10", APIBL.getKey("10", 4));
            while (APIBL.READE("10", APIBL.getKey("10", 4))) {
               found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
               // Check if put away
               if (PAGRS.getAUT2() == 2) {
                  PPRTVQTYDS.clear();
                  PPRTVQTYDS.setP6CONO(APIBL.getCONO());
                  PPRTVQTYDS.setP6REPN(APIBL.getREPN());
                  PPRTVQTYDS.setP6PGNM().moveLeft("PPS350");
                  PPRTVQTYDS.setP6CHKF(3);
                  PPRTVQTYDS.setP6PUNO().move(APIBL.getPUNO());
                  PPRTVQTYDS.setP6PNLI(APIBL.getPNLI());
                  PPRTVQTYDS.setP6PNLS(APIBL.getPNLS());
                  pPPRTVQTYpreCall();
                  apCall("PPRTVQTY", pPPRTVQTY);
                  pPPRTVQTYpostCall();
                  if (!isBlank(PPRTVQTYDS.getP6RPQA(), cRefORQA.decimals())) {
                     allRequiredLinesPutAway = false;
                     break;
                  }
               }
            }
            found_MPAGRS = false;
            if (!allRequiredLinesPutAway) {
               picSetMethod('D');
               IN60 = true;
               // MSGID=AP05008 Update is only permitted after put-away is performed
               COMPMQ("AP05008");
               return;
            }
         }
         picSetMethod('U');
         // Call APS455 Supplier invoice batch. Select operation
         if (!callAPS455(APIBH.getDIVI(), APIBH.getINBN(), cRefIBOPext.UPDATE_TO_APL())) {
            picSetMethod('D');
            IN60 = true;
         }
         return;
      }
      // Aggregation drill down
      if (XXOPT2.EQ("10")) {
         if (CRMNGVW.drillDown(this, SYSOR, APIBH, DSP.WWNFTR, DSP.S0NAGG, DSP.W1OBKV, 
                  DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, 
                  DSP.W7OBKV, DSP.W8OBKV, DSP.W9OBKV, DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV)) {
            DSP.WWNFTR++;
         }
         return;
      }
      // Reverse printout
      if (this.XXOPT2.EQ("20")) {
         CRCommon.setChangeMode();
         picSetMethod('U');
         // Call APS450Fnc, Reverse printout
         // =========================================
         pReversePrintout = get_pReversePrintout();
         pReversePrintout.messages.forgetNotifications();
         pReversePrintout.prepare();
         // Set input parameters
         // - Division
         pReversePrintout.DIVI.set().moveLeftPad(APIBH.getDIVI());
         // - Supplier invoice batch number
         pReversePrintout.INBN.set(APIBH.getINBN());
         // =========================================
         int transStatus = executeTransaction(pReversePrintout.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pReversePrintout.messages, transStatus);
         // =========================================
         // Handle messages
         handleMessages(pReversePrintout.messages, 'B', true);
         // Release resources allocated by the parameter list.
         pReversePrintout.release();
         return;
      } 
      // Reject Invoice
      if (this.XXOPT2.EQ("21")) {
         picSetMethod('U');
         CRCommon.setSpecialPanelSequence("N", saveSEQ);
         picPush(this.SEQ.charAt(0), 'I');
         return;
      }
      // Approve Invoice
      if (this.XXOPT2.EQ("22")) {
         picSetMethod('U');
         CRCommon.setSpecialPanelSequence("M", saveSEQ);
         picPush(this.SEQ.charAt(0), 'I');
         return;
      }
      // Reset to status new
      if (this.XXOPT2.EQ("23")) {
         CRCommon.setChangeMode();
         picSetMethod('U');
         // Call APS450Fnc, Reset to status 10 - NEW
         // =========================================
         pResetToStatusNew = get_pResetToStatusNew();
         pResetToStatusNew.messages.forgetNotifications();
         pResetToStatusNew.prepare();
         // Set input parameters
         // - Division
         pResetToStatusNew.DIVI.set().moveLeftPad(APIBH.getDIVI());
         // - Supplier invoice batch number
         pResetToStatusNew.INBN.set(APIBH.getINBN());
         // =========================================
         int transStatus = executeTransaction(pResetToStatusNew.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pResetToStatusNew.messages, transStatus);
         // =========================================
         // Handle messages
         handleMessages(pResetToStatusNew.messages, 'B', true);
         // Release resources allocated by the parameter list.
         pResetToStatusNew.release();
         return;
      }
      // Acknowledge
      if (this.XXOPT2.EQ("24")) {
         picSetMethod('U');
         CRCommon.setSpecialPanelSequence("L", saveSEQ);
         picPush(this.SEQ.charAt(0), 'I');
         return;
      }
      // Option 11 - 19
      if (this.XXOPT2.EQ("11") ||
         this.XXOPT2.EQ("12") ||
         this.XXOPT2.EQ("13") ||
         this.XXOPT2.EQ("14")) {
         // Option 13
         if (this.XXOPT2.EQ("13")) {
            // Display Claim lines
            if (APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM()) &&
                APIBH.getIBTP().NE(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
               picSetMethod('D');
               IN60 = true;
               // MSGID=X_00050 &1 (Option &2) cannot be used for invoice type &3
               COMPMQ("X_00050", formatToString(SRCOMRCM.getMessage("X__7793", "MVXCON")), formatToString(this.XXOPT2), formatToString(APIBH.getIBTP()));
               return;
            }
         }
         CRCommon.setChangeMode();
         picSetMethod('U');
         picPush(this.XXOP22.getChar(), 'I');
         return;
      }
   }

   /**
   * B-panel - update.
   */
   public void PBUPD() {
      ensureReadOfTables();
      lastPanel = 'B'; // Set last panel
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      moveToIN(/*dstOffset*/ 31, savedIndicators31to40); // Restore indicators 31-40
      moveToIN(/*dstOffset*/ 61, savedIndicators61to90); // Restore indicators 61-90
      DSP.clearOption();
      // Restore panel sequence after setSpecialPanelSequence
      CRCommon.restorePanelSequence(saveSEQ);
      // Next step
      if (DSP.WBRRNA != 0) {
         picSetMethod('C');
      } else {
         picSetMethod('I');
      }
      // No subfile record
      if (DSP.WBRRNA == 0) {
         return;
      }
      // F12 was pressed - stop processing subfile lines
      if (F12 == DSP.X0FKEY) {
         picSetMethod('D');
         this.XLPIC1 = picGetPrevTopPanel();
         // Check if copy aborted.
         if (this.XLPIC1 == 'C') {
            DSP.updateSFL("BS");
            return;
         }
      }
      // Update just option on subfile line if display mode
      if (this.XXOPT2.EQ(" 5")) {
         DSP.XBRRNA = DSP.WBRRNA;
         if (DSP.S0NAGG > 1) {
            // Reverse image on aggregated line
            IN66 = true;
         }
         DSP.updateSFL("BS");
         IN66 = false;
         return;
      }
      // Move new values to subfile (if not copy)
      if (this.XXOPT2.NE(" 3")) {
         if (APIBH.CHAIN("00", APIBH.getKey("00"))) {
            DSP.XBRRNA = DSP.WBRRNA;
         } else {
            IN50 = true;
            IN51 = true;
            DSP.XBRRNA = DSP.XBRRNT;
         }
         PBMSF();
         if (this.XXOPT2.EQ(" 2")) {
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         }
         if (DSP.S0NAGG > 1) {
            // Reverse image on aggregated line
            IN66 = true;
         }
         DSP.updateSFL("BS");
         IN66 = false;
         IN50 = false;
         IN51 = false;
         return;
      }
      // Copy
      DSP.updateSFL("BS");
      // Check if copy was completed
      if (APIBH.CHAIN("00", APIBH.getKey("00"))) {
         DSP.XBRRNM++;
         DSP.XBRRNA = DSP.XBRRNM;
         DSP.WBRRNA = DSP.XBRRNM;
         DSP.WSOPT2.clear();
         PBMSF();
         DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         DSP.writeSFL("BS");
         IN95 = true;
      }
   }

   /**
   * B-panel - roll subfile.
   */
   public void PBROL() {
      int aggregation = 0;
      int numberOfRecords = 0;
      int maxRecords = CRMNGVW.getMaxRecords(SYVIU);
      int savedNumberOfAggregetadeLines = 0;
      int linesWithNoAuthority = 0;
      ensureReadOfTables();
      DSP.XBRRNA = DSP.XBRRNM;
      boolean firstRecord = true;
      DSP.restPosSFL('B');
      // Restore last position
      if (DSP.XBRRNM > 0) {
         APIBH.setRecord(saved_FAPIBH);
         if (IN70 || IN67 || IN68) {
            mainTableSelection = CRMNGVW.setSelection(mainTableSelection, SYIBC.getFILE(), SYVIU.getSLF1(), SYVIU.getSLF2(), SYVIU.getSLF3(), SYSOR);
         }
         PXAOPT.move("SETGT");
         PXAKNO = CRMNGVW.getNumberOfKeyFields();
         accessTable();
         PBROL_SETGT(); // exit point for modification
      }
      //   Read records
      PXAOPT.move("READE");
      PXAKNO = CRMNGVW.getNumberOfFilterFields();
      accessTable();
      PBROL_READE1(); // exit point for modification
      if (DSP.XBRRNA == 0) {
         // First line
         CRMNGVW.initSubTotal(PXVAL1, PXVAL2, PXVAL3, PXVAL4, PXVAL5, PXVAL6, PXVAL7, PXVAL8, PXVAL9, 
            PXVA10, PXVA11, PXVA12, PXVA13, PXVA14, PXVA15, PXVA16);
      }
      while (!IN93 && this.XXSPGB > 0 ||
             (CRMNGVW.isSubTotalToBeDisplayed() || CRMNGVW.isDelayedSubtotalCreated()) && this.XXSPGB > 0){
         if (CRMNGVW.isSubTotalToBeDisplayed()) {
            // Display subtotal line (or last line if aggregation and no subtotals)
            DSP.S0NAGG = CRMNGVW.createSubTotalLine(DSP.S0QTTP, DSP.S0NAGG); // Return number of aggregated records to DSP.S0NAGG
            PBMSF_BeforeSFLPost();
            DSP.moveDSintoSFLpost("B");// do standard sync for SFL buf
            this.XXSPGB--;
            DSP.XBRRNA++;
            DSP.WBRRNA = DSP.XBRRNA;
            DSP.XBRRNM++;
            DSP.WSOPT2.clear();
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
            setHiddenFieldsInSubfileRecord();
            if (DSP.S0NAGG > 1) {
               // Reverse image on aggregated line
               IN66 = true;
            }
            DSP.writeSFL("BS");
            IN66 = false;
         } else {
            if (APIBH.isQualifiedForSFL(WSRGTM)) {
               if (isValidLineAuthority()) {
                  aggregation = CRMNGVW.checkAggregation(DSP.WWNFTR, DSP.WWAGGR, SYVIU.getAGRG(), 
                    PXVAL1, PXVAL2, PXVAL3, PXVAL4, PXVAL5, PXVAL6, PXVAL7, PXVAL8, PXVAL9, PXVA10, 
                    PXVA11, PXVA12, PXVA13, PXVA14, PXVA15, PXVA16, IN93);
                  PBROL_PBMSF(); // exit point for modification
                  PBMSF();
                  if (DSP.WWAGGR != 0 &&
                     (aggregation == 1 ||
                     firstRecord)) {
                     // Save for delayed update if aggregation is active
                     savedLine.moveLeftPad(CRMNGVW.SFL);
                     CRMNGVW.saveColumnUpdateIndicators("savedLine", DSP.S0NAGG); // Save update indicators and save number of aggregated records
                     saved_FAPIBH.setRecord(APIBH);
                  } else {
                     if (DSP.WWAGGR != 0) {
                        // Set aggregated line as current if aggregation is active
                        savedNumberOfAggregetadeLines = DSP.S0NAGG;
                        DSP.S0QTTP.moveLeftPad(savedLine);
                        DSP.S0NAGG = CRMNGVW.restoreColumnUpdateIndicators("savedLine"); // Restore update indicators and restore number of aggregated records
                        PBMSF_BeforeSFLPost();
                        DSP.moveDSintoSFLpost("B");// do standard sync for SFL buf
                        savedLine.moveLeftPad(CRMNGVW.SFL);
                        CRMNGVW.saveColumnUpdateIndicators("savedLine", savedNumberOfAggregetadeLines);
                        saved_FAPIBH.setRecord(APIBH);
                     }
                     if (aggregation == 0) {
                        // Count, only if this line should not be aggregated
                        this.XXSPGB--;
                        setHiddenFieldsInSubfileRecord();
                     }
                     DSP.XBRRNA++;
                     DSP.WBRRNA = DSP.XBRRNA;
                     DSP.XBRRNM++;
                     if (DSP.WWAGGR == 0) {
                        saved_FAPIBH.setRecord(APIBH);
                     }
                     DSP.WSOPT2.clear();
                     DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
                     if (DSP.S0NAGG > 1) {
                        // Reverse image on aggregated line
                        IN66 = true;
                     }
                     DSP.writeSFL("BS");
                     IN66 = false;
                     CRMNGVW.activateDelayedSubTotal(DSP.WWAGGR, IN93);
                  }
               } else {
                  linesWithNoAuthority++;
               }
               firstRecord = false;
            }
            if (!CRMNGVW.isLastRecord()) {
               PXAOPT.move("READE");
               PXAKNO = CRMNGVW.getNumberOfFilterFields();
               accessTable();
               numberOfRecords++;
               if (maxRecords < numberOfRecords) {
                  IN93 = true;
               }
            }
            PBROL_READE2(); // exit point for modification
            CRMNGVW.subTotal(SYSOR, DSP.WWNFTR, SYVIU.getSUB1(), SYVIU.getSUB2(), SYVIU.getSUB3(), 
                  PXVAL1, PXVAL2, PXVAL3, PXVAL4, PXVAL5, PXVAL6, PXVAL7, PXVAL8, PXVAL9, 
                  PXVA10, PXVA11, PXVA12, PXVA13, PXVA14, PXVA15, PXVA16, IN93);
            CRMNGVW.checkSubtotal(DSP.WWAGGR, IN93, SYVIU.getSUB1(), SYVIU.getSUB2(), SYVIU.getSUB3(), firstRecord, savedLine);
         }
      }
      if (IN93) {
         if (maxRecords < numberOfRecords) {
            IN96 = false;
            picSetMethod('D');
            //   MSGID=XMX0801 The number of read records &1 is greater than the entere
            COMPMQ("XMX0801", formatToString(numberOfRecords, 15));
         } else {
            IN96 = true;
            DSP.writeEofSFL("BS");
         }
      }
      if (linesWithNoAuthority > 0) { 	
         IN60 = true; 	
         //   MSGID=XAU0011 Some line(s) are not displayed due to missing authority 	
         COMPMQ("XAU0011"); 	
      }
      // If records exists then display the subfile
      if (DSP.XBRRNM > 0) {
         IN95 = true;
         DSP.XBRRNA = DSP.XBRRNM - 1;
         DSP.trunkPosSFL('B');
         DSP.XBRRNA++;
      }
   }

   /** 
   * @return
   *    True if the user is authorized to work with the current record.
   */ 
   public boolean isValidLineAuthority() {
      return CRMNGVW.isValidLineAuthority(APIBH.getDIVI(), null, null);
   }

   /**
   * B-panel - move information to subfile fields.
   */
   public void PBMSF() {
      DSP.moveDSintoSFLpre("B"); // Do standard sync for SFL buf
      PBMSF_AfterSFLPre();  // exit point for modification
      // Get info from APS450Fnc
      pMaintain = get_pMaintain();
      pMaintain.messages.forgetNotifications();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.setQuickMode();
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = true;
      passExtensionTable(); // exit point for modification
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      pMaintain.release();
      // Read tables required in the view
      if (CRMNGVW.hasMDB("CIDMAS")) {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, APIBH.getCONO(), APIBH.getSUNO());
      }
      if (CRMNGVW.hasMDB("CIDVEN")) {
         foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
         foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
         cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, APIBH.getCONO(), APIBH.getDIVI(), retrieveSUNO(APIBH.getSUNO(), APIBH.getSPYN()));
         found_CIDVEN = foundParam_CIDVEN.getBoolean();
         found_CSUDIV = foundParam_CSUDIV.getBoolean();
      }
      CRMNGVW.readRelatedTables(APIBH, CM100, CM101, SYIBC.getIBCA(), SYVIU.getSOPT(), SYSOR);
      // Override no of decimals 	 	 	
      setOverridingDecimals();
      // Calculate all virtual fields for this line 	 	 	
      CRMNGVW.calculateVirtualFields(SYIBC.getIBCA(), APIBH, CM100, SYIBC, DSP.WBRRNA); 	
      // Create subfile line 	 	 	
      CRMNGVW.line(SYIBC.getIBCA(), CM100, CM101, DSP.WBRRNA);
      // Set work in progress flag - BIST
      CRMNGVW.setValue("E5BIST", pMaintain.BIST.get()); // BIST is not accessible if locked in other explicit transaction
      // Protect fields
      if (this.PXO.charAt(1) == '0') {
         // Change authority needed for input
         CRMNGVW.protectAllEditFields();
      } else {
         pMaintain.parameters.prepareGetNext();
         for (cParameter param = pMaintain.parameters.getNext(); param != null; 
            param = pMaintain.parameters.getNext()) 
         {
            if (param.isAccessOUTorDISABLED()) {
               CRMNGVW.protectEditField("E5" + param.getName());
            }
         }
      }
      // Set values in virtual fields (&-fields) 	 	 	
      setVirtualFieldValues();
      DSP.S0NAGG = CRMNGVW.calculateAggregation(); // Create aggregated info and return number of aggregated records on this line
      DSP.S0QTTP.moveLeft(CRMNGVW.getLine());
      // =====================================================================
      moveFromIN(DSP.S0INDI, /*fromInd*/ 1); // Save protect indicators
      DSP.S0CHNO = pMaintain.work_CHNO; // Save change number
      PBMSF_BeforeSFLPost(); // exit point for modification
      DSP.moveDSintoSFLpost("B"); // Do standard sync for SFL buf
   }

   /**
   * B-panel - prompt.
   */
   public boolean PBPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PBPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * B-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PBPMT_perform() {
      // Prompt sorting order 	 	 	 	
      if (DSP.hasFocus("WWQTTP")) { 	 	 	 	
         DSP.WWQTTP = CRMNGVW.promptSortingOrder(this, DSP, "WWQTTP", DSP.WWQTTP, SYVIU, blankIBCA, singleDivision, sortingChanged);
         if (sortingChanged.getBoolean()) {
            XXQTTP = -(9);
         }
         return true; 	
      } 	 	 	
      // Prompt view 	 	 	
      if (DSP.hasFocus("WOPAVR")) { 	 	 	
         CRMNGVW.promptView(this, DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, SYSPV, SYSPP, 'B', DSP.WWQTTP); 	 	 	
         return true;
      } 	 	
      // Prompt object values
      if (DSP.hasFocus("WFSLCT") ||
          DSP.hasFocus("WTSLCT") ||
          DSP.hasFocus("WFSLC2") ||
          DSP.hasFocus("WTSLC2") ||
          DSP.hasFocus("WFSLC3") ||
          DSP.hasFocus("WTSLC3") ||
          DSP.hasFocus("W1OBKV") ||
          DSP.hasFocus("W2OBKV") ||
          DSP.hasFocus("W3OBKV") ||
          DSP.hasFocus("W4OBKV") ||
          DSP.hasFocus("W5OBKV") ||
          DSP.hasFocus("W6OBKV") ||
          DSP.hasFocus("W7OBKV") ||
          DSP.hasFocus("W8OBKV") ||
          DSP.hasFocus("W9OBKV") ||
          DSP.hasFocus("WAOBKV") ||
          DSP.hasFocus("WBOBKV") ||
          DSP.hasFocus("WCOBKV")) {
         // Generic prompts in list head (metadata in CRCRTPMD)
         return CRMNGVW.browseListHead(this, DSP, SYVIU, SYSOR, SYKEY, SYIBC.getMGRP());
      }
      return false;
   }

   /**
   * Updates with values entered on the subfile line.
   * Sets focus to the subfile line if an info message is issued.
   * @return
   *    False if an info message is issued.
   */
   public boolean updateFromSflB() {
      boolean infoMessage = false;
      boolean newEntryContext = false;
      // Restore change number
      APIBH.setCHNO(DSP.S0CHNO); 
      // Call APS450Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.indicateAutomated();
      // Set key parameters
      // Pass a reference to the record.
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = true;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      // Call APS450Fnc, maintain - mode CHANGE, step VALIDATE
      // =========================================
      do {
         pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
         // Move fields to function parameters
         if (!setParametersFromListB(DSP.WBRRNA)) {
            return false;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pMaintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
         newEntryContext = pMaintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pMaintain.release();
         // Return error message
         if (infoMessage) {
            return false;
         }
      } while (newEntryContext);
      // Call APS450Fnc, maintain - mode CHANGE, step UPDATE
      // =========================================
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      APIBH.CHAIN("00", APIBH.getKey("00"));
      return true;
   }   

   /**
   * Sets parameters in pMaintain from edit-fields in list B.
   * Issues an info message if an error occurs and sets focus to the field.
   *
   * @param lineNo
   *    Line number in the subfile
   * @return
   *    Returns false an error occurs.
   */
   public boolean setParametersFromListB(int lineNo) {
      // Prepare CRMNGVW
      CRMNGVW.setLine(DSP.S0QTTP);
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      setOverridingDecimals();
      // Division
      if (CRMNGVW.hasField("E5DIVI")) {
         if (!CRMNGVW.isProtected("E5DIVI")) {
            pMaintain.DIVI.set().moveLeftPad(CRMNGVW.getString("E5DIVI"));
         }
      }
      // Invoice batch number
      if (CRMNGVW.hasField("E5INBN")) {
         if (!CRMNGVW.isProtected("E5INBN")) {
            pMaintain.INBN.set(CRMNGVW.getLong("E5INBN"));
         }
      }
      // Invoice batch status
      if (CRMNGVW.hasField("E5BIST")) {
         if (!CRMNGVW.isProtected("E5BIST")) {
            pMaintain.BIST.set(CRMNGVW.getInt("E5BIST"));
         }
      }
      // Invoice batch type
      if (CRMNGVW.hasField("E5IBTP")) {
         if (!CRMNGVW.isProtected("E5IBTP")) {
            pMaintain.IBTP.set().moveLeftPad(CRMNGVW.getString("E5IBTP"));
         }
      }
      // Invoice batch head errors
      if (CRMNGVW.hasField("E5IBHE")) {
         if (!CRMNGVW.isProtected("E5IBHE")) {
            pMaintain.IBHE.set(CRMNGVW.getInt("E5IBHE"));
         }
      }
      // Invoice batch line errors
      if (CRMNGVW.hasField("E5IBLE")) {
         if (!CRMNGVW.isProtected("E5IBLE")) {
            pMaintain.IBLE.set(CRMNGVW.getInt("E5IBLE"));
         }
      }
      // Supplier accepted
      if (CRMNGVW.hasField("E5SUPA")) {
         if (!CRMNGVW.isProtected("E5SUPA")) {
            pMaintain.SUPA.set(CRMNGVW.getInt("E5SUPA"));
         }
      }
      // Supplier invoice number
      if (CRMNGVW.hasField("E5SINO")) {
         if (!CRMNGVW.isProtected("E5SINO")) {
            pMaintain.SINO.set().moveLeftPad(CRMNGVW.getString("E5SINO"));
         }
      }
      // Payee
      if (CRMNGVW.hasField("E5SPYN")) {
         if (!CRMNGVW.isProtected("E5SPYN")) {
            pMaintain.SPYN.set().moveLeftPad(CRMNGVW.getString("E5SPYN"));
         }
      }
      // Supplier
      if (CRMNGVW.hasField("E5SUNO")) {
         if (!CRMNGVW.isProtected("E5SUNO")) {
            pMaintain.SUNO.set().moveLeftPad(CRMNGVW.getString("E5SUNO"));
         }
      }
      // Invoice date
      if (CRMNGVW.hasField("E5IVDT")) {
         if (!CRMNGVW.isProtected("E5IVDT")) {
            pMaintain.IVDT.set(CRMNGVW.getDate("E5IVDT"));
            if (pMaintain.IVDT.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("IVDT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WIVD101 Invoice date &1 is invalid
               COMPMQ("WIVD101", CRMNGVW.getString("E5IVDT"));
               return false;
            }
         }
      }
      // Currency
      if (CRMNGVW.hasField("E5CUCD")) {
         if (!CRMNGVW.isProtected("E5CUCD")) {
            pMaintain.CUCD.set().moveLeftPad(CRMNGVW.getString("E5CUCD"));
         }
      }
      // Rate
      if (CRMNGVW.hasField("E5ARAT")) {
         if (!CRMNGVW.isProtected("E5ARAT")) {
            pMaintain.ARAT.set(CRMNGVW.getDouble("E5ARAT"));
            if (CRMNGVW.getErrorCode("E5ARAT") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("ARAT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5ARAT"), 1));
               return false;
            }
         }
      }
      // Payment terms
      if (CRMNGVW.hasField("E5TEPY")) {
         if (!CRMNGVW.isProtected("E5TEPY")) {
            pMaintain.TEPY.set().moveLeftPad(CRMNGVW.getString("E5TEPY"));
         }
      }
      // Payment method AP
      if (CRMNGVW.hasField("E5PYME")) {
         if (!CRMNGVW.isProtected("E5PYME")) {
            pMaintain.PYME.set().moveLeftPad(CRMNGVW.getString("E5PYME"));
         }
      }
      // Bank account ID
      if (CRMNGVW.hasField("E5BKID")) {
         if (!CRMNGVW.isProtected("E5BKID")) {
            pMaintain.BKID.set().moveLeftPad(CRMNGVW.getString("E5BKID"));
         }
      }
      // Trade code
      if (CRMNGVW.hasField("E5TDCD")) {
         if (!CRMNGVW.isProtected("E5TDCD")) {
            pMaintain.TDCD.set().moveLeftPad(CRMNGVW.getString("E5TDCD"));
         }
      }
      // Currency amount
      if (CRMNGVW.hasField("E5CUAM")) {
         if (!CRMNGVW.isProtected("E5CUAM")) {
            pMaintain.CUAM.set(CRMNGVW.getDouble("E5CUAM"));
            if (CRMNGVW.getErrorCode("E5CUAM") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CUAM");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CUAM"), 1));
               return false;
            } else {
            }
         }
      }
      // VAT amount
      if (CRMNGVW.hasField("E5VTAM")) {
         if (!CRMNGVW.isProtected("E5VTAM")) {
            pMaintain.VTAM.set(CRMNGVW.getDouble("E5VTAM"));
            if (CRMNGVW.getErrorCode("E5VTAM") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("VTAM");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5VTAM"), 1));
               return false;
            }
         }
      }
      // Voucher number
      if (CRMNGVW.hasField("E5VONO")) {
         if (!CRMNGVW.isProtected("E5VONO")) {
            pMaintain.VONO.set(CRMNGVW.getInt("E5VONO"));
         }
      }
      // Voucher number series
      if (CRMNGVW.hasField("E5VSER")) {
         if (!CRMNGVW.isProtected("E5VSER")) {
            pMaintain.VSER.set().moveLeftPad(CRMNGVW.getString("E5VSER"));
         }
      }
      // Accounting date
      if (CRMNGVW.hasField("E5ACDT")) {
         if (!CRMNGVW.isProtected("E5ACDT")) {
            pMaintain.ACDT.set(CRMNGVW.getDate("E5ACDT"));
            if (pMaintain.ACDT.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("ACDT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WACD101 Accounting date &1 is invalid
               COMPMQ("WACD101", CRMNGVW.getString("E5ACDT"));
               return false;
            }
         }
      }
      // Authorizer
      if (CRMNGVW.hasField("E5APCD")) {
         if (!CRMNGVW.isProtected("E5APCD")) {
            pMaintain.APCD.set().moveLeftPad(CRMNGVW.getString("E5APCD"));
         }
      }
      // Invoice matching
      if (CRMNGVW.hasField("E5IMCD")) {
         if (!CRMNGVW.isProtected("E5IMCD")) {
            pMaintain.IMCD.set().moveLeftPad(CRMNGVW.getString("E5IMCD"));
         }
      }
      // Service code
      if (CRMNGVW.hasField("E5SERS")) {
         if (!CRMNGVW.isProtected("E5SERS")) {
            pMaintain.SERS.set(CRMNGVW.getInt("E5SERS"));
         }
      }
      // Due date
      if (CRMNGVW.hasField("E5DUDT")) {
         if (!CRMNGVW.isProtected("E5DUDT")) {
            pMaintain.DUDT.set(CRMNGVW.getDate("E5DUDT"));
            if (pMaintain.DUDT.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("DUDT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WDUD101 Accounting date &1 is invalid
               COMPMQ("WDUD101", CRMNGVW.getString("E5DUDT"));
               return false;
            }
         }
      }
      // Future exchange contract
      if (CRMNGVW.hasField("E5FECN")) {
         if (!CRMNGVW.isProtected("E5FECN")) {
            pMaintain.FECN.set().moveLeftPad(CRMNGVW.getString("E5FECN"));
         }
      }
      // Currency rate typr
      if (CRMNGVW.hasField("E5CRTP")) {
         if (!CRMNGVW.isProtected("E5CRTP")) {
            pMaintain.CRTP.set(CRMNGVW.getInt("E5CRTP"));
         }
      }
      // From/To country
      if (CRMNGVW.hasField("E5FTCO")) {
         if (!CRMNGVW.isProtected("E5FTCO")) {
            pMaintain.FTCO.set().moveLeftPad(CRMNGVW.getString("E5FTCO"));
         }
      }
      // Base country
      if (CRMNGVW.hasField("E5BSCD")) {
         if (!CRMNGVW.isProtected("E5BSCD")) {
            pMaintain.BSCD.set().moveLeftPad(CRMNGVW.getString("E5BSCD"));
         }
      }
      // Tot line amount
      if (CRMNGVW.hasField("E5TLNA")) {
         if (!CRMNGVW.isProtected("E5TLNA")) {
            pMaintain.TLNA.set(CRMNGVW.getDouble("E5TLNA"));
            if (CRMNGVW.getErrorCode("E5TLNA") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("TLNA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5TLNA"), 1));
               return false;
            }
         }
      }
      // Total charges
      if (CRMNGVW.hasField("E5TCHG")) {
         if (!CRMNGVW.isProtected("E5TCHG")) {
            pMaintain.TCHG.set(CRMNGVW.getDouble("E5TCHG"));
            if (CRMNGVW.getErrorCode("E5TCHG") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("TCHG");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5TCHG"), 1));
               return false;
            }
         }
      }
      // Total due
      if (CRMNGVW.hasField("E5TOPA")) {
         if (!CRMNGVW.isProtected("E5TOPA")) {
            pMaintain.TOPA.set(CRMNGVW.getDouble("E5TOPA"));
            if (CRMNGVW.getErrorCode("E5TOPA") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("TOPA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5TOPA"), 1));
               return false;
            }
         }
      }
      // Purchase order number
      if (CRMNGVW.hasField("E5PUNO")) {
         if (!CRMNGVW.isProtected("E5PUNO")) {
            pMaintain.PUNO.set().moveLeftPad(CRMNGVW.getString("E5PUNO"));
         }
      }
      // Order date
      if (CRMNGVW.hasField("E5PUDT")) {
         if (!CRMNGVW.isProtected("E5PUDT")) {
            pMaintain.PUDT.set(CRMNGVW.getDate("E5PUDT"));
            if (pMaintain.PUDT.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("PUDT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WPUD101 Order date &1 is invalid
               COMPMQ("WPUD101", CRMNGVW.getString("E5PUDT"));
               return false;
            }
         }
      }
      // Path
      if (CRMNGVW.hasField("E5PATH")) {
         if (!CRMNGVW.isProtected("E5PATH")) {
            pMaintain.PATH.set().moveLeftPad(CRMNGVW.getString("E5PATH"));
         }
      }
      // Cash discount terms
      if (CRMNGVW.hasField("E5TECD")) {
         if (!CRMNGVW.isProtected("E5TECD")) {
            pMaintain.TECD.set().moveLeftPad(CRMNGVW.getString("E5TECD"));
         }
      }
      // Cash discount date 1
      if (CRMNGVW.hasField("E5CDT1")) {
         if (!CRMNGVW.isProtected("E5CDT1")) {
            pMaintain.CDT1.set(CRMNGVW.getDate("E5CDT1"));
            if (pMaintain.CDT1.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDT1");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WCD1001 Cash discount date 1 &1 is invalid
               COMPMQ("WCD1001", CRMNGVW.getString("E5CDT1"));
               return false;
            }
         }
      }
      // Cash discount date 2
      if (CRMNGVW.hasField("E5CDT2")) {
         if (!CRMNGVW.isProtected("E5CDT2")) {
            pMaintain.CDT2.set(CRMNGVW.getDate("E5CDT2"));
            if (pMaintain.CDT2.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDT2");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WCD1101 Cash discount date 2 &1 is invalid
               COMPMQ("WCD1101", CRMNGVW.getString("E5CDT2"));
               return false;
            }
         }
      }
      // Cash discount date 3
      if (CRMNGVW.hasField("E5CDT3")) {
         if (!CRMNGVW.isProtected("E5CDT3")) {
            pMaintain.CDT3.set(CRMNGVW.getDate("E5CDT3"));
            if (pMaintain.CDT3.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDT3");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WCD1201 Cash discount date 3 &1 is invalid
               COMPMQ("WCD1201", CRMNGVW.getString("E5CDT3"));
               return false;
            }
         }
      }
      // Cash discount amount 1
      if (CRMNGVW.hasField("E5CDC1")) {
         if (!CRMNGVW.isProtected("E5CDC1")) {
            pMaintain.CDC1.set(CRMNGVW.getDouble("E5CDC1"));
            if (CRMNGVW.getErrorCode("E5CDC1") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDC1");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDC1"), 1));
               return false;
            }
         }
      }
      // Cash discount amount 2
      if (CRMNGVW.hasField("E5CDC2")) {
         if (!CRMNGVW.isProtected("E5CDC2")) {
            pMaintain.CDC2.set(CRMNGVW.getDouble("E5CDC2"));
            if (CRMNGVW.getErrorCode("E5CDC2") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDC2");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDC2"), 1));
               return false;
            }
         }
      }
      // Cash discount amount 3
      if (CRMNGVW.hasField("E5CDC3")) {
         if (!CRMNGVW.isProtected("E5CDC3")) {
            pMaintain.CDC3.set(CRMNGVW.getDouble("E5CDC3"));
            if (CRMNGVW.getErrorCode("E5CDC3") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDC3");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDC3"), 1));
               return false;
            }
         }
      }
      // Cash discount percentage 1
      if (CRMNGVW.hasField("E5CDP1")) {
         if (!CRMNGVW.isProtected("E5CDP1")) {
            pMaintain.CDP1.set(CRMNGVW.getDouble("E5CDP1"));
            if (CRMNGVW.getErrorCode("E5CDP1") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDP1");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDP1"), 1));
               return false;
            }
         }
      }
      // Cash discount percentage 2
      if (CRMNGVW.hasField("E5CDP2")) {
         if (!CRMNGVW.isProtected("E5CDP2")) {
            pMaintain.CDP2.set(CRMNGVW.getDouble("E5CDP2"));
            if (CRMNGVW.getErrorCode("E5CDP2") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDP2");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDP2"), 1));
               return false;
            }
         }
      }
      // Cash discount percentage 3
      if (CRMNGVW.hasField("E5CDP3")) {
         if (!CRMNGVW.isProtected("E5CDP3")) {
            pMaintain.CDP3.set(CRMNGVW.getDouble("E5CDP3"));
            if (CRMNGVW.getErrorCode("E5CDP3") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("CDP3");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5CDP3"), 1));
               return false;
            }
         }
      }
      // Document code
      if (CRMNGVW.hasField("E5DNCO")) {
         if (!CRMNGVW.isProtected("E5DNCO")) {
            pMaintain.DNCO.set().moveLeftPad(CRMNGVW.getString("E5DNCO"));
         }
      }
      // Supplier acceptance
      if (CRMNGVW.hasField("E5SUAC")) {
         if (!CRMNGVW.isProtected("E5SUAC")) {
            pMaintain.SUAC.set(CRMNGVW.getInt("E5SUAC"));
         }
      }
      // Conditions for adding lines
      if (CRMNGVW.hasField("E5SBAD")) {
         if (!CRMNGVW.isProtected("E5SBAD")) {
            pMaintain.SBAD.set(CRMNGVW.getInt("E5SBAD"));
         }
      }
      // Invoice per receiving number
      if (CRMNGVW.hasField("E5UPBI")) {
         if (!CRMNGVW.isProtected("E5UPBI")) {
            pMaintain.UPBI.set(CRMNGVW.getInt("E5UPBI"));
         }
      }
      // AP Standard document
      if (CRMNGVW.hasField("E5SDAP")) {
         if (!CRMNGVW.isProtected("E5SDAP")) {
            pMaintain.SDAP.set().moveLeftPad(CRMNGVW.getString("E5SDAP"));
         }
      }
      // Debit note reason
      if (CRMNGVW.hasField("E5DNRE")) {
         if (!CRMNGVW.isProtected("E5DNRE")) {
            pMaintain.DNRE.set().moveLeftPad(CRMNGVW.getString("E5DNRE"));
         }
      }
      // Our invoicing address
      if (CRMNGVW.hasField("E5PYAD")) {
         if (!CRMNGVW.isProtected("E5PYAD")) {
            pMaintain.PYAD.set().moveLeftPad(CRMNGVW.getString("E5PYAD"));
         }
      }
      // Text line 1
      if (CRMNGVW.hasField("E5SDA1")) {
         if (!CRMNGVW.isProtected("E5SDA1")) {
            pMaintain.SDA1.set().moveLeftPad(CRMNGVW.getString("E5SDA1"));
         }
      }
      // Text line 2
      if (CRMNGVW.hasField("E5SDA2")) {
         if (!CRMNGVW.isProtected("E5SDA2")) {
            pMaintain.SDA2.set().moveLeftPad(CRMNGVW.getString("E5SDA2"));
         }
      }
      // Text line 3
      if (CRMNGVW.hasField("E5SDA3")) {
         if (!CRMNGVW.isProtected("E5SDA3")) {
            pMaintain.SDA3.set().moveLeftPad(CRMNGVW.getString("E5SDA3"));
         }
      }
      // Total taxable amount
      if (CRMNGVW.hasField("E5TTXA")) {
         if (!CRMNGVW.isProtected("E5TTXA")) {
            pMaintain.TTXA.set(CRMNGVW.getDouble("E5TTXA"));
            if (CRMNGVW.getErrorCode("E5TTXA") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("TTXA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5TTXA"), 1));
               return false;
            }
         }
      }
      // Cash discount base
      if (CRMNGVW.hasField("E5TASD")) {
         if (!CRMNGVW.isProtected("E5TASD")) {
            pMaintain.TASD.set(CRMNGVW.getDouble("E5TASD"));
            if (CRMNGVW.getErrorCode("E5TASD") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("TASD");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5TASD"), 1));
               return false;
            }
         }
      }
      // Pre-paid amount
      if (CRMNGVW.hasField("E5PRPA")) {
         if (!CRMNGVW.isProtected("E5PRPA")) {
            pMaintain.PRPA.set(CRMNGVW.getDouble("E5PRPA"));
            if (CRMNGVW.getErrorCode("E5PRPA") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("PRPA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E5PRPA"), 1));
               return false;
            }
         }
      }
      // Tax applicable
      if (CRMNGVW.hasField("E5TXAP")) {
         if (!CRMNGVW.isProtected("E5TXAP")) {
            pMaintain.TXAP.set(CRMNGVW.getInt("E5TXAP"));
         }
      }
      // Geographical code
      if (CRMNGVW.hasField("E5GEOC")) {
         if (!CRMNGVW.isProtected("E5GEOC")) {
            pMaintain.GEOC.set(CRMNGVW.getInt("E5GEOC"));
         }
      }
      // Tax included
      if (CRMNGVW.hasField("E5TXIN")) {
         if (!CRMNGVW.isProtected("E5TXIN")) {
            pMaintain.TXIN.set(CRMNGVW.getInt("E5TXIN"));
         }
      }
      // EAN location code payee
      if (CRMNGVW.hasField("E5EALP")) {
         if (!CRMNGVW.isProtected("E5EALP")) {
            pMaintain.EALP.set().moveLeftPad(CRMNGVW.getString("E5EALP"));
         }
      }
      // EAN location code consignee
      if (CRMNGVW.hasField("E5EALR")) {
         if (!CRMNGVW.isProtected("E5EALR")) {
            pMaintain.EALR.set().moveLeftPad(CRMNGVW.getString("E5EALR"));
         }
      }
      // EAN location code supplier
      if (CRMNGVW.hasField("E5EALS")) {
         if (!CRMNGVW.isProtected("E5EALS")) {
            pMaintain.EALS.set().moveLeftPad(CRMNGVW.getString("E5EALS"));
         }
      }
      // VAT reg no
      if (CRMNGVW.hasField("E5VRNO")) {
         if (!CRMNGVW.isProtected("E5VRNO")) {
            pMaintain.VRNO.set().moveLeftPad(CRMNGVW.getString("E5VRNO"));
         }
      }
      // Delivery date
      if (CRMNGVW.hasField("E5DEDA")) {
         if (!CRMNGVW.isProtected("E5DEDA")) {
            pMaintain.DEDA.set(CRMNGVW.getDate("E5DEDA"));
            if (pMaintain.DEDA.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("DEDA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WDEDA01 Delivery date &1 is invalid
               COMPMQ("WDEDA01", CRMNGVW.getString("E5DEDA"));
               return false;
            }
         }
      }
      // Original invoice number
      if (CRMNGVW.hasField("E5DNOI")) {
         if (!CRMNGVW.isProtected("E5DNOI")) {
            pMaintain.DNOI.set().moveLeftPad(CRMNGVW.getString("E5DNOI"));
         }
      }
      // Original year
      if (CRMNGVW.hasField("E5OYEA")) {
         if (!CRMNGVW.isProtected("E5OYEA")) {
            pMaintain.OYEA.set(CRMNGVW.getInt("E5OYEA"));
         }
      }
      // Reference number
      if (CRMNGVW.hasField("E5PPYR")) {
         if (!CRMNGVW.isProtected("E5PPYR")) {
            pMaintain.PPYR.set().moveLeftPad(CRMNGVW.getString("E5PPYR"));
         }
      }
      // Payment request number
      if (CRMNGVW.hasField("E5PPYN")) {
         if (!CRMNGVW.isProtected("E5PPYN")) {
            pMaintain.PPYN.set().moveLeftPad(CRMNGVW.getString("E5PPYN"));
         }
      }
      // Year
      if (CRMNGVW.hasField("E5YEA4")) {
         if (!CRMNGVW.isProtected("E5YEA4")) {
            pMaintain.YEA4.set(CRMNGVW.getInt("E5YEA4"));
         }
      }
      return true;
   }

   /**
   * Handles messages returned from the function program.
   *
   * @param lineNo
   *    Line number in the subfile
   * @param messages
   *    A reference to the messages.
   * @return
   *    Returns true if a message occured. 
   */
   public boolean handleMessagesInListB(int lineNo, cCRMessageList messages) {
      // handle messages
      // (Don't issue notifications in an open subfile).
      if (messages.exist()) {
         // Check for errors
         messages.prepareGetNext();
         if (messages.getNextError(CRMessageDS)) {
            // Issue message
            setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
            CRCommon.COMPMQ(CRMessageDS);
            return true;
         }
      }
      return false;
   }

   /**
   * Sets focus on a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   */
   public void setFocus_on_subfile_B(int lineNo) { 	
      // Set focus on the line
      DSP.setSubfileFocus("BS", "*ROW", lineNo);
   }

   /**
   * Sets focus on a field in a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   * @param FLDI
   *    Field ID
   */
   public void setFocus_on_subfile_B(int lineNo, MvxString FLDI) { 	
      // Set focus on the field
      String fieldName = "E5" + FLDI.toStringRTrim();
      if (CRMNGVW.hasField(fieldName) && !CRMNGVW.isProtected(fieldName)) {
         DSP.setSubfileFocus("BS", fieldName, lineNo); 	
      } else {
         // Try to set focus on first open field instead
         boolean openColumnFound = false;
         MvxString columnName = null;
         for (int column = 0; column < 30; column++) {
            columnName = CRMNGVW.getField(column);
            if (columnName.isBlank()) {
               break;
            }
            if (!CRMNGVW.isProtected(columnName)) {
               openColumnFound = true;
               break;
            }
         }
         if (openColumnFound) {
            DSP.setSubfileFocus("BS", columnName.toStringRTrim(), lineNo); 	
         } else {
            DSP.setSubfileFocus("BS", "*ROW", lineNo);
         }
      }
   } 	

   /**
   * C-panel - initiate.
   */
   public void PCINZ() {
      preparePanelC = true;
      // Call APS450Fnc, copy - step initiate
      // =========================================
      pCopy = get_pCopy();
      pCopy.messages.forgetNotifications();
      pCopy.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pCopy.INBN.set(APIBH.getINBN());
         // - Division
         pCopy.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pCopy.APIBH = APIBH;
      pCopy.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
   }

   /**
   * C-panel - display - initiate.
   */
   public void PCDSP_INZ() {
      // Set last panel
      lastPanel = 'C';
      // Check if the display fields should be prepared
      if (preparePanelC) {
         preparePanelC = false;
         PCDSP_INZ_prepare();
      }
   }

   /**
   * C-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PCDSP_INZ_prepare() {
      // Division
      DSP.WCDIVI.moveLeftPad(pCopy.DIVI.get());
      IN03 = pCopy.DIVI.isAccessOUT();
      IN23 = pCopy.DIVI.isAccessDISABLED();
      // Copy to Division
      DSP.CPDIVI.moveLeftPad(pCopy.CPDIVI.get());
      IN03 = pCopy.CPDIVI.isAccessOUT();
      IN23 = pCopy.CPDIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WCINBN = pCopy.INBN.get();
      // Copy to Invoice batch number
      DSP.CPINBN = pCopy.CPINBN.get();
   }

   /**
   * C-panel - check.
   */
   public void PCCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, delete - step validate
      // =========================================
      pCopy.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PCCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelC = true;
      // =========================================
      // Handle messages
      handleMessages(pCopy.messages, 'C', false);
      if (pCopy.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pCopy.release();
   }

   /**
   * C-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PCCHK_prepare() {
      // Copy to Invoice batch number
      if (pCopy.CPINBN.isAccessMANDATORYorOPTIONAL()) {
         pCopy.CPDIVI.set().moveLeftPad(DSP.CPDIVI);
         pCopy.CPINBN.set(DSP.CPINBN);
      }
      return true;
   }

   /**
   * C-panel - update.
   */
   public void PCUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, maintain - step update
      // =========================================
      pCopy.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pCopy.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pCopy.messages, transStatus);
      preparePanelC = true;
      // =========================================
      // Handle messages
      error = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
      // Next step
      if (!error) {
         if (this.PXO.charAt(1) == '1') {
            // Update copied data
            this.SQ = 1;
            picSet(this.SEQ.charAt(this.SQ - 1), 'I');
            CRCommon.setChangeMode();
         } else {
            // Return to start panel
            picPop();
         }
         // Set new key values
         APIBH.setDIVI().moveLeftPad(pCopy.CPDIVI.get());
         APIBH.setINBN(pCopy.CPINBN.get());
      }
   }

   /**
   * C-panel - prompt.
   */
   public boolean PCPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PCPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * C-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PCPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt invoice batch number
      if (DSP.hasFocus("CPINBN")) {
         if (cRefINBNext.prompt(this, /*maintain*/ false, /*select*/ pCopy.CPINBN.isAccessMANDATORYorOPTIONAL(), 
             APIBH, APIBH.getCONO(), APIBH.getDIVI(), DSP.CPINBN)) 
         {
            DSP.CPINBN = this.PXKVA3.getLong(cRefINBN.length(), 0);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_C(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WCDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WCINBN");
      } else if (FLDI.EQ("CPDIVI")) {  DSP.setFocus("CPDIVI");
      } else if (FLDI.EQ("CPINBN")) {  DSP.setFocus("CPINBN");
      }
   }

   /**
   * D-panel - initiate.
   */
   public void PDINZ() {
      preparePanelD = true;
      // Call APS450Fnc, delete - step initiate
      // =========================================
      pDelete = get_pDelete();
      pDelete.messages.forgetNotifications();
      pDelete.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pDelete.INBN.set(APIBH.getINBN());
         // - Division
         pDelete.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pDelete.APIBH = APIBH;
      pDelete.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
   }

   /**
   * D-panel - display - initiate.
   */
   public void PDDSP_INZ() {
      // Set last panel
      lastPanel = 'D';
      // Check if the display fields should be prepared
      if (preparePanelD) {
         preparePanelD = false;
         PDDSP_INZ_prepare();
      }
   }

   /**
   * D-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PDDSP_INZ_prepare() {
      // Division
      DSP.WDDIVI.moveLeftPad(pDelete.DIVI.get());
      IN03 = pDelete.DIVI.isAccessOUT();
      IN23 = pDelete.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WDINBN = pDelete.INBN.get();
      // Supplier invoice number
      DSP.WDSINO.moveLeftPad(pDelete.SINO.get());
      // Payee
      DSP.WDSPYN.moveLeftPad(pDelete.SPYN.get());
      // Supplier
      DSP.WDSUNO.moveLeftPad(pDelete.SUNO.get());
      // Invoice date
      DSP.WDIVDT.moveLeft(CRCalendar.convertDate(pDelete.DIVI.get(), pDelete.IVDT.get(), LDAZD.DTFM, ' '));
      // Currency amount
      this.PXDCCD = pDelete.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pDelete.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WDCUAM.moveRight(this.PXALPH);
      // Currency
      DSP.WDCUCD.moveLeftPad(pDelete.CUCD.get());
      // Voucher number
      DSP.WDVONO = pDelete.VONO.get();
      // Display warning if batch payment hasn't been updated in AP, before del
      if (pDelete.VONO.get() == 0) {
         // MSGID=X_00060 This invoice has not been entered in Accounts Payable
         COMPMQ("X_00060");
      }
      // Accounting date
      DSP.WDACDT.moveLeft(CRCalendar.convertDate(pDelete.DIVI.get(), pDelete.ACDT.get(), LDAZD.DTFM, ' '));
   }

   /**
   * D-panel - check.
   */
   public void PDCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, delete - step validate
      // =========================================
      pDelete.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PDCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelD = true;
      // =========================================
      // Handle messages
      handleMessages(pDelete.messages, 'D', false);
      if (pDelete.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pDelete.release();
   }

   /**
   * D-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PDCHK_prepare() {
      // Nothing to validate
      return true;
   }

   /**
   * D-panel - update.
   */
   public void PDUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, delete - step update
      // =========================================
      pDelete.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pDelete.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pDelete.messages, transStatus);
      preparePanelD = true;
      // =========================================
      // Handle messages
      error = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
      // Next step
      if (!error) {
         picPop();
      }
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_D(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WDINBN");
      }
   }

   /**
   * E-panel - initiate.
   */
   public void PEINZ() {
      preparePanelE = true;
      // Call APS450Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pMaintain.INBN.set(APIBH.getINBN());
         // - Division
         pMaintain.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
      // Save payee
      lastSPYN.moveLeftPad(pMaintain.SPYN.get());
   }

   /**
   * E-panel - display - initiate.
   */
   public void PEDSP_INZ() {
      // Set last panel
      lastPanel = 'E';
      // Check if text exists
      IN57 = pMaintain.TXID.get() != 0L;
      // Check if the display fields should be prepared
      if (preparePanelE) {
         preparePanelE = false;
         PEDSP_INZ_prepare();
      }
   }

   /**
   * E-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PEDSP_INZ_prepare() {
      // Set dynamic drop down for IBTP
      CRCommon.setDropDown("WEIBTP", pMaintain.IBTP);
      // Division
      DSP.WEDIVI.moveLeftPad(pMaintain.DIVI.get());
      IN03 = pMaintain.DIVI.isAccessOUT();
      IN23 = pMaintain.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WEINBN = pMaintain.INBN.get();
      IN02 = pMaintain.INBN.isAccessOUT();
      IN22 = pMaintain.INBN.isAccessDISABLED();
      // Status invoice
      DSP.WEBIST = pMaintain.BIST.get();
      IN04 = pMaintain.BIST.isAccessOUT();
      IN24 = pMaintain.BIST.isAccessDISABLED();
      // Invoice head error
      DSP.WEIBHE = pMaintain.IBHE.get();
      IN04 = pMaintain.IBHE.isAccessOUT();
      IN24 = pMaintain.IBHE.isAccessDISABLED();
      // Invoice line error
      DSP.WEIBLE = pMaintain.IBLE.get();
      IN04 = pMaintain.IBLE.isAccessOUT();
      IN24 = pMaintain.IBLE.isAccessDISABLED();
      // Invoice batch type
      DSP.WEIBTP.moveLeftPad(pMaintain.IBTP.get());
      IN08 = pMaintain.IBTP.isAccessOUT();
      IN28 = pMaintain.IBTP.isAccessDISABLED();
      // Invoice status
      DSP.WESUPA = pMaintain.SUPA.get();
      IN04 = pMaintain.SUPA.isAccessOUT();
      IN24 = pMaintain.SUPA.isAccessDISABLED();
      // Message
      DSP.WEMSGD.moveLeftPad(pMaintain.MSGD.get());
      IN04 = pMaintain.MSGD.isAccessOUT();
      IN24 = pMaintain.MSGD.isAccessDISABLED();
      // Supplier invoice number
      DSP.WESINO.moveLeftPad(pMaintain.SINO.get());
      IN05 = pMaintain.SINO.isAccessOUT();
      IN25 = pMaintain.SINO.isAccessDISABLED();
      // Payee
      DSP.WESPYN.moveLeftPad(pMaintain.SPYN.get());
      IN06 = pMaintain.SPYN.isAccessOUT();
      IN26 = pMaintain.SPYN.isAccessDISABLED();
      // Supplier
      DSP.WESUNO.moveLeftPad(pMaintain.SUNO.get());
      IN07 = pMaintain.SUNO.isAccessOUT();
      IN27 = pMaintain.SUNO.isAccessDISABLED();
      // Invoice date
      DSP.WEIVDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.IVDT.get(), LDAZD.DTFM, ' '));
      IN12 = pMaintain.IVDT.isAccessOUT();
      IN32 = pMaintain.IVDT.isAccessDISABLED();
      // Currency
      DSP.WECUCD.moveLeftPad(pMaintain.CUCD.get());
      IN11 = pMaintain.CUCD.isAccessOUT();
      IN31 = pMaintain.CUCD.isAccessDISABLED();
      // Exchange rate
      this.PXDCCD = pMaintain.ARAT.getDecimals();
      this.PXFLDD = 12 + this.PXDCCD;
      this.PXEDTC = '4';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.ARAT.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEARAT.moveRight(this.PXALPH);
      IN01 = pMaintain.ARAT.isAccessOUT();
      IN21 = pMaintain.ARAT.isAccessDISABLED();
      // Payment terms
      DSP.WETEPY.moveLeftPad(pMaintain.TEPY.get());
      IN01 = pMaintain.TEPY.isAccessOUT();
      IN21 = pMaintain.TEPY.isAccessDISABLED();
      // Payment method AP
      DSP.WEPYME.moveLeftPad(pMaintain.PYME.get());
      IN01 = pMaintain.PYME.isAccessOUT();
      IN21 = pMaintain.PYME.isAccessDISABLED();
      // Trade code
      DSP.WETDCD.moveLeftPad(pMaintain.TDCD.get());
      IN64 = pMaintain.TDCD.isAccessOUT();
      IN74 = pMaintain.TDCD.isAccessDISABLED();
      // Trade code description
      DSP.WEDSCR.moveLeftPad(pMaintain.DSCR.get());
      IN63 = pMaintain.DSCR.isAccessOUT();
      IN73 = pMaintain.DSCR.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pMaintain.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WECUAM.moveRight(this.PXALPH);
      IN15 = pMaintain.CUAM.isAccessOUT();
      IN35 = pMaintain.CUAM.isAccessDISABLED();
      // VAT amount
      IN09 = pMaintain.VTAM.isAccessOUT();
      IN29 = pMaintain.VTAM.isAccessDISABLED();
      if (!IN29) { 	
         this.PXDCCD = pMaintain.VTAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = pMaintain.VTAM.get();
         this.PXALPH.clear();
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         DSP.WEVTAM.moveRight(this.PXALPH);
      }
      // Voucher number
      DSP.WEVONO = pMaintain.VONO.get();
      IN17 = pMaintain.VONO.isAccessOUT();
      IN37 = pMaintain.VONO.isAccessDISABLED();
      // Voucher number series
      DSP.WEVSER.moveLeftPad(pMaintain.VSER.get());
      IN18 = pMaintain.VSER.isAccessOUT();
      IN38 = pMaintain.VSER.isAccessDISABLED();
      // Accounting date
      if (pMaintain.ACDT.get() == 0 &&
          pMaintain.VONO.get() != 0) {
         //  ACDT should be initiated with todays date if blank when VONO is entered
         pMaintain.ACDT.set(movexDate());
      }
      DSP.WEACDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.ACDT.get(), LDAZD.DTFM, ' '));
      IN01 = pMaintain.ACDT.isAccessOUT();
      IN21 = pMaintain.ACDT.isAccessDISABLED();
      // Authorizer
      DSP.WEAPCD.moveLeftPad(pMaintain.APCD.get());
      IN01 = pMaintain.APCD.isAccessOUT();
      IN21 = pMaintain.APCD.isAccessDISABLED();
      // Invoice matching
      DSP.WEIMCD = pMaintain.IMCD.get().getChar();
      IN14 = pMaintain.IMCD.isAccessOUT();
      IN34 = pMaintain.IMCD.isAccessDISABLED();
      // Service code
      DSP.WESERS = pMaintain.SERS.get();
      IN01 = pMaintain.SERS.isAccessOUT();
      IN21 = pMaintain.SERS.isAccessDISABLED();
      // Due date
      DSP.WEDUDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.DUDT.get(), LDAZD.DTFM, ' '));
      IN01 = pMaintain.DUDT.isAccessOUT();
      IN21 = pMaintain.DUDT.isAccessDISABLED();
      // Future rate agreement
      DSP.WEFECN.moveLeftPad(pMaintain.FECN.get());
      IN01 = pMaintain.FECN.isAccessOUT();
      IN21 = pMaintain.FECN.isAccessDISABLED();
      // Exchange rate type
      DSP.WECRTP = pMaintain.CRTP.get();
      IN01 = pMaintain.CRTP.isAccessOUT();
      IN21 = pMaintain.CRTP.isAccessDISABLED();
      // From/To country
      DSP.WEFTCO.moveLeftPad(pMaintain.FTCO.get());
      IN20 = pMaintain.FTCO.isAccessOUT();
      IN40 = pMaintain.FTCO.isAccessDISABLED();
      // Base country
      DSP.WEBSCD.moveLeftPad(pMaintain.BSCD.get());
      IN20 = pMaintain.BSCD.isAccessOUT();
      IN40 = pMaintain.BSCD.isAccessDISABLED();
      // Total line amount
      this.PXDCCD = pMaintain.TLNA.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.TLNA.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WETLNA.moveRight(this.PXALPH);
      // TLNA is always read only 
      IN61 = pMaintain.TLNA.isAccessOUT();
      IN71 = pMaintain.TLNA.isAccessDISABLED();
      // Total charges
      this.PXDCCD = pMaintain.TCHG.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.TCHG.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WETCHG.moveRight(this.PXALPH);
      IN13 = pMaintain.TCHG.isAccessOUT();
      IN33 = pMaintain.TCHG.isAccessDISABLED();
      // Total due
      this.PXDCCD = pMaintain.TOPA.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.TOPA.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WETOPA.moveRight(this.PXALPH);
      IN16 = pMaintain.TOPA.isAccessOUT();
      IN36 = pMaintain.TOPA.isAccessDISABLED();
      // Purchase order number
      DSP.WEPUNO.moveLeftPad(pMaintain.PUNO.get());
      IN62 = pMaintain.PUNO.isAccessOUT();
      IN72 = pMaintain.PUNO.isAccessDISABLED();
      // Order date
      DSP.WEPUDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.PUDT.get(), LDAZD.DTFM, ' '));
      IN10 = pMaintain.PUDT.isAccessOUT();
      IN30 = pMaintain.PUDT.isAccessDISABLED();
      // Display document button 
      IN39 = !pMaintain.PATH.get().isBlank();
      // CorrelationID
      DSP.WECORI.moveLeftPad(pMaintain.CORI.get());
      // Entry date
      DSP.WERGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WELMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WECHID.moveLeftPad(pMaintain.CHID.get());
   }

   /**
   * E-panel - display - roll.
   */
   public void PEDSP_X0E78R() {
      roll(DSP.WEDIVI, DSP.WEINBN, "WEINBN");
   }

   /**
   * E-panel - check.
   */
   public void PECHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PECHK_prepare()) {
         return;
      }
      // Check if new payee default values should be retrieved
      pMaintain.GPDF.set(false);
      if (CRCommon.getMode() == cEnumMode.ADD && pMaintain.TEPY.isAccessDISABLED()) {
         // Get payee defaults
         pMaintain.GPDF.set(true);
      } else {
         if (DSP.WESPYN.NE(lastSPYN)) {
            // Display confirmation popup
            PXMNS920 = get_PXMNS920();
            PXMNS920.clear();
            PXMNS920.PXMID1.move("AP10017");
            PXMNS920.PXMSF1.moveLeft("MVXMSG");
            PXMNS920.PXCNFM = '1';
            this.PXPGNM.moveLeft(this.DSPGM);
            PXMNS920.MNS920();
            if (PXMNS920.PXCNFE == ' ') {
               // Get payee defaults
               pMaintain.GPDF.set(true);
            } else {
               // Display message about cancel of get default values
               picSetMethod('D');
               lastSPYN.moveLeftPad(DSP.WESPYN);
               return;
            }
         }
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelE = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'E', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Save payee
      lastSPYN.moveLeftPad(pMaintain.SPYN.get());
   }

   /**
   * E-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PECHK_prepare() {
      // Division
      if (pMaintain.DIVI.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.DIVI.set().moveLeftPad(DSP.WEDIVI);
         if (CRCommon.isMultiDivisionCompany()) {
            //   Check access authority for division
            PLCHKAD.ADCONO = LDAZD.CONO;
            PLCHKAD.ADCMTP = LDAZD.CMTP;
            PLCHKAD.ADFDIV.move(pMaintain.DIVI.get());
            PLCHKAD.ADTDIV.clear();
            PLCHKAD.ADRESP.move(LDAZD.RESP);
            IN92 = PLCHKAD.CCHKACD();
            if (PLCHKAD.ADAERR == 1) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEDIVI");
               COMPMQ(PLCHKAD.ADMSGI.toString(), PLCHKAD.ADMSGD);
               return false;
            }
         }
      }
      // Status invoice
      if (pMaintain.BIST.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.BIST.set(DSP.WEBIST);
      }
      // Invoice batch type
      if (pMaintain.IBTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IBTP.set().moveLeftPad(DSP.WEIBTP);
      }
      // Supplier accepted
      if (pMaintain.SUPA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUPA.set(DSP.WESUPA);
      }
      // Supplier invoice number
      if (pMaintain.SINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SINO.set().moveLeftPad(DSP.WESINO);
      }
      // Payee
      if (pMaintain.SPYN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SPYN.set().moveLeftPad(DSP.WESPYN);
      }
      // Supplier
      if (pMaintain.SUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUNO.set().moveLeftPad(DSP.WESUNO);
      }
      // Invoice date
      if (pMaintain.IVDT.isAccessMANDATORYorOPTIONAL()) {
         if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEIVDT, LDAZD.DTFM)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEIVDT");
            // MSGID=WIVD101 Invoice date &1 is invalid
            COMPMQ("WIVD101", DSP.WEIVDT);
            return false;
         }
         pMaintain.IVDT.set(CRCalendar.getDate());
      }
      // Currency
      if (pMaintain.CUCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CUCD.set().moveLeftPad(DSP.WECUCD);
      }
      // Exchange rate
      if (pMaintain.ARAT.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.ARAT.getDecimals();
         this.PXFLDD = 11 + this.PXDCCD;
         this.PXEDTC = '4';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEARAT);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEARAT");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.ARAT.set(this.PXNUM);
      }
      // Payment terms
      if (pMaintain.TEPY.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.TEPY.set().moveLeftPad(DSP.WETEPY);
      }
      // Payment method AP
      if (pMaintain.PYME.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PYME.set().moveLeftPad(DSP.WEPYME);
      }
      // Trade code
      if (pMaintain.TDCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.TDCD.set().moveLeftPad(DSP.WETDCD);
      }
      // Trade code description
      if (pMaintain.DSCR.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.DSCR.set().moveLeftPad(DSP.WEDSCR);
      }
      // Currency amount
      if (pMaintain.CUAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CUAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WECUAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WECUAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CUAM.set(this.PXNUM);
      }
      // VAT amount
      if (pMaintain.VTAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEVTAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEVTAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTAM.set(this.PXNUM);
      }
      // Voucher number
      if (pMaintain.VONO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.VONO.set(DSP.WEVONO);
      }
      // Voucher number series
      if (pMaintain.VSER.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.VSER.set().moveLeftPad(DSP.WEVSER);
      }
      // Accounting date
      if (pMaintain.ACDT.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WEACDT.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEACDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEACDT");
               // MSGID=WACD101 Accounting date &1 is invalid
               COMPMQ("WACD101", DSP.WEACDT);
               return false;
            }
            pMaintain.ACDT.set(CRCalendar.getDate());
         } else {
            pMaintain.ACDT.set(0);
         }
      }
      // Authorizer
      if (pMaintain.APCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.APCD.set().moveLeftPad(DSP.WEAPCD);
      }
      // Invoice matching
      if (pMaintain.IMCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IMCD.set().moveLeftPad(DSP.WEIMCD);
      }
      // Service code
      if (pMaintain.SERS.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SERS.set(DSP.WESERS);
      }
      // Due date
      if (pMaintain.DUDT.isAccessMANDATORYorOPTIONAL()) {
         if (DSP.WEDUDT.isBlank()) {
            pMaintain.DUDT.clearValue();
         } else {
            if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEDUDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEDUDT");
               // MSGID=WDUD101 Due date &1 is invalid
               COMPMQ("WDUD101", DSP.WEDUDT);
               return false;
            }
            pMaintain.DUDT.set(CRCalendar.getDate());
         }
      }
      // Future exchange rate agreement
      if (pMaintain.FECN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.FECN.set().moveLeftPad(DSP.WEFECN);
      }
      // Currency rate type
      if (pMaintain.CRTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CRTP.set(DSP.WECRTP);
      }
      // From/To country
      if (pMaintain.FTCO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.FTCO.set().moveLeftPad(DSP.WEFTCO);
      }
      // Base country
      if (pMaintain.BSCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.BSCD.set().moveLeftPad(DSP.WEBSCD);
      }
      // Total line amount
      if (pMaintain.TLNA.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.TLNA.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WETLNA);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WETLNA");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.TLNA.set(this.PXNUM);
      }
      // Total charges
      if (pMaintain.TCHG.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.TCHG.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WETCHG);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WETCHG");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.TCHG.set(this.PXNUM);
      }
      // Total due
      if (pMaintain.TOPA.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.TOPA.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WETOPA);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WETOPA");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.TOPA.set(this.PXNUM);
      }
      // Purchase order number
      if (pMaintain.PUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PUNO.set().moveLeftPad(DSP.WEPUNO);
      }
      // Purchase order date
      if (pMaintain.PUDT.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WEPUDT.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEPUDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEPUDT");
               // MSGID=WPUD101 Order date date &1 is invalid
               COMPMQ("WPUD101", DSP.WEPUDT);
               return false;
            }
            pMaintain.PUDT.set(CRCalendar.getDate());
         }
      }
      // Path
      if (pMaintain.PATH.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PATH.set().moveLeftPad(DSP.WEPATH);
      }
      return true;
   }

   /**
   * E-panel - update.
   */
   public void PEUPD() {
      // Declaration
      boolean error = false;
      // Only update in last panel in add mode.
      if (CRCommon.getMode() == cEnumMode.ADD) {
         picSetMethod('D');
         picPush(seqSwitchToNextPanel(), 'I');
         return;
      }
      // Call APS450Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      preparePanelE = true;
      // =========================================
      // Handle messages
      error = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         if (CRCommon.getMode() == cEnumMode.ADD) {
            picSetMethod('D');
         }
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * E-panel - prompt.
   */
   public boolean PEPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PEPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * E-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PEPMT_perform() {
      // ----------------------------------------------------------------
      if (CRCommon.isCentralUser()) {
         if (DSP.hasFocus("WEDIVI")) {
            // Prompt Division
            if (cRefDIVIext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.DIVI.isAccessMANDATORYorOPTIONAL(), 
                MNDIV, APIBH.getCONO(), DSP.WEDIVI)) 
            {
               DSP.WEDIVI.moveLeftPad(this.PXKVA2);
            }
            return true;
         }
      }
      // ----------------------------------------------------------------
      // Prompt Supplier
      if (DSP.hasFocus("WESUNO")) {
         if (cRefSUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.SUNO.isAccessMANDATORYorOPTIONAL(), 
             IDMAS, APIBH.getCONO(), DSP.WESUNO)) 
         {
            DSP.WESUNO.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Payee
      if (DSP.hasFocus("WESPYN")) {
         if (cRefSUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.SPYN.isAccessMANDATORYorOPTIONAL(), 
             IDMAS, APIBH.getCONO(), DSP.WESPYN)) 
         {
            DSP.WESPYN.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Currency
      if (DSP.hasFocus("WECUCD")) {
         if (cRefCUCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.CUCD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WECUCD)) 
         {
            DSP.WECUCD.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Terms of payment
      if (DSP.hasFocus("WETEPY")) {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, false, APIBH.getCONO(), DSP.WESPYN);
         if (cRefTEPYext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.TEPY.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WETEPY, IDMAS.getLNCD())) 
         {
            DSP.WETEPY.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Payment method - AP
      if (DSP.hasFocus("WEPYME")) {
         if (cRefPYMEext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PYME.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WEPYME)) 
         {
            DSP.WEPYME.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Trade code
      if (DSP.hasFocus("WETDCD")) {
         if (cRefTDCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.TDCD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WEDIVI, DSP.WETDCD)) 
         {
            DSP.WETDCD.moveLeftPad(this.PXKVA4);
            found_CSYTAB_TDCD = cRefTDCDext.getCSYTAB_TDCD(SYTAB, false, LDAZD.CMTP, LDAZD.CONO, DSP.WEDIVI, DSP.WETDCD);
            cRefTDCDext.setAdditionalInfo(SYTAB, DSP.WEDSCR, null);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Voucher number series
      if (DSP.hasFocus("WEVSER")) {
         if (cRefVSERext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.VSER.isAccessMANDATORYorOPTIONAL(), 
             SYNBV, APIBH.getCONO(), DSP.WEDIVI, DSP.WEVSER)) 
         {
            DSP.WEVSER.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Future exchange contract
      if (DSP.hasFocus("WEFECN")) {
         if (cRefFECNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.FECN.isAccessMANDATORYorOPTIONAL(), 
             FUEXC, APIBH.getCONO(), DSP.WEDIVI, DSP.WEFECN)) 
         {
            DSP.WEFECN.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Authorized user
      if (DSP.hasFocus("WEAPCD")) {
         if (cRefAPCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.APCD.isAccessMANDATORYorOPTIONAL(), 
             APRCD, APIBH.getCONO(), DSP.WEDIVI, DSP.WEAPCD)) 
         {
            DSP.WEAPCD.moveLeftPad(this.PXKVA3);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Currency rate type
      if (DSP.hasFocus("WECRTP")) {
         if (cRefCRTPext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.CRTP.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WEDIVI, DSP.WECRTP)) 
         {
            DSP.WECRTP = this.PXKVA4.getIntLeft(cRefCRTP.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Base country
      if (DSP.hasFocus("WEBSCD")) {
         if (cRefBSCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.BSCD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WEBSCD)) 
         {
            DSP.WEBSCD.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // From/To country
      if (DSP.hasFocus("WEFTCO")) {
         if (cRefFTCOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.FTCO.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WEFTCO)) 
         {
            DSP.WEFTCO.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Service code
      if (DSP.hasFocus("WESERS")) {
         if (cRefSERSext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.SERS.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WESERS)) 
         {
            DSP.WESERS = this.PXKVA4.getIntLeft(cRefSERS.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt purchase order
      if (DSP.hasFocus("WEPUNO")) {
         if (cRefPUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PUNO.isAccessMANDATORYorOPTIONAL(), 
             PHEAD, APIBH.getCONO(), DSP.WEPUNO)) 
         {
            DSP.WEPUNO.moveLeft(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("BIST")
          || FLDI.EQ("IBTP")
          || FLDI.EQ("IBHE")
          || FLDI.EQ("IBLE")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("MSGD")
          || FLDI.EQ("IMCD")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("SPYN")
          || FLDI.EQ("IVDT")
          || FLDI.EQ("DUDT")
          || FLDI.EQ("FECN")
          || FLDI.EQ("SERS")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("TLNA")
          || FLDI.EQ("TCHG")
          || FLDI.EQ("ARAT")
          || FLDI.EQ("TOPA")
          || FLDI.EQ("CRTP")
          || FLDI.EQ("PYME")
          || FLDI.EQ("ACDT")
          || FLDI.EQ("TEPY")
          || FLDI.EQ("APCD")
          || FLDI.EQ("PATH")
          || FLDI.EQ("FTCO")
          || FLDI.EQ("BSCD")
          || FLDI.EQ("PUNO")
          || FLDI.EQ("PUDT")
          || FLDI.EQ("TDCD")
          || FLDI.EQ("DSCR")
          || FLDI.EQ("VONO")
          || FLDI.EQ("VSER")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WEDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WEINBN");
      } else if (FLDI.EQ("BIST")) {  DSP.setFocus("WEBIST");
      } else if (FLDI.EQ("IBTP")) {  DSP.setFocus("WEIBTP");
      } else if (FLDI.EQ("IBHE")) {  DSP.setFocus("WEIBHE");
      } else if (FLDI.EQ("IBLE")) {  DSP.setFocus("WEIBLE");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WESUPA");
      } else if (FLDI.EQ("MSGD")) {  DSP.setFocus("WEMSGD");
      } else if (FLDI.EQ("IMCD")) {  DSP.setFocus("WEIMCD");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WESINO");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WESUNO");
      } else if (FLDI.EQ("SPYN")) {  DSP.setFocus("WESPYN");
      } else if (FLDI.EQ("IVDT")) {  DSP.setFocus("WEIVDT");
      } else if (FLDI.EQ("DUDT")) {  DSP.setFocus("WEDUDT");
      } else if (FLDI.EQ("FECN")) {  DSP.setFocus("WEFECN");
      } else if (FLDI.EQ("SERS")) {  DSP.setFocus("WESERS");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WECUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WECUCD");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WEVTAM");
      } else if (FLDI.EQ("TLNA")) {  DSP.setFocus("WETLNA");
      } else if (FLDI.EQ("TCHG")) {  DSP.setFocus("WETCHG");
      } else if (FLDI.EQ("ARAT")) {  DSP.setFocus("WEARAT");
      } else if (FLDI.EQ("TOPA")) {  DSP.setFocus("WETOPA");
      } else if (FLDI.EQ("CRTP")) {  DSP.setFocus("WECRTP");
      } else if (FLDI.EQ("PYME")) {  DSP.setFocus("WEPYME");
      } else if (FLDI.EQ("ACDT")) {  DSP.setFocus("WEACDT");
      } else if (FLDI.EQ("TEPY")) {  DSP.setFocus("WETEPY");
      } else if (FLDI.EQ("APCD")) {  DSP.setFocus("WEAPCD");
      } else if (FLDI.EQ("PATH")) {  DSP.setFocus("WEPATH");
      } else if (FLDI.EQ("FTCO")) {  DSP.setFocus("WEFTCO");
      } else if (FLDI.EQ("BSCD")) {  DSP.setFocus("WEBSCD");
      } else if (FLDI.EQ("PUNO")) {  DSP.setFocus("WEPUNO");
      } else if (FLDI.EQ("PUDT")) {  DSP.setFocus("WEPUDT");
      } else if (FLDI.EQ("TDCD")) {  DSP.setFocus("WETDCD");
      } else if (FLDI.EQ("DSCR")) {  DSP.setFocus("WEDSCR");
      } else if (FLDI.EQ("VONO")) {  DSP.setFocus("WEVONO");
      } else if (FLDI.EQ("VSER")) {  DSP.setFocus("WEVSER");
      }
   }

   /**
   * F-panel - initiate.
   */
   public void PFINZ() {
      preparePanelF = true;
      // Only initiate in E-panel in add mode.
      if (CRCommon.getMode() == cEnumMode.ADD) {
         return;
      }
      // Call APS450Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pMaintain.INBN.set(APIBH.getINBN());
         // - Division
         pMaintain.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'F', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
   }

   /**
   * F-panel - display - initiate.
   */
   public void PFDSP_INZ() {
      // Set last panel
      lastPanel = 'F';
      // Check if text exists
      IN57 = pMaintain.TXID.get() != 0L;
      // Check if the display fields should be prepared
      if (preparePanelF) {
         preparePanelF = false;
         PFDSP_INZ_prepare();
      }
   }

   /**
   * F-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PFDSP_INZ_prepare() {
      // Set dynamic drop down for IBTP
      CRCommon.setDropDown("WFIBTP", pMaintain.IBTP);
      // Division
      DSP.WFDIVI.moveLeftPad(pMaintain.DIVI.get());
      IN03 = pMaintain.DIVI.isAccessOUT();
      IN23 = pMaintain.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WFINBN = pMaintain.INBN.get();
      IN02 = pMaintain.INBN.isAccessOUT();
      IN22 = pMaintain.INBN.isAccessDISABLED();
      // Supplier invoice number
      DSP.WFSINO.moveLeftPad(pMaintain.SINO.get());
      IN04 = pMaintain.SINO.isAccessOUT();
      IN24 = pMaintain.SINO.isAccessDISABLED();
      // Invoice batch type
      DSP.WFIBTP.moveLeftPad(pMaintain.IBTP.get());
      IN04 = pMaintain.IBTP.isAccessOUT();
      IN24 = pMaintain.IBTP.isAccessDISABLED();
      // Cash discount terms
      DSP.WFTECD.moveLeftPad(pMaintain.TECD.get());
      IN05 = pMaintain.TECD.isAccessOUT();
      IN25 = pMaintain.TECD.isAccessDISABLED();
      // Cash discount date 1
      DSP.WFCDT1.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.CDT1.get(), LDAZD.DTFM, ' '));
      IN05 = pMaintain.CDT1.isAccessOUT();
      IN25 = pMaintain.CDT1.isAccessDISABLED();
      // Cash discount date 2
      DSP.WFCDT2.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.CDT2.get(), LDAZD.DTFM, ' '));
      IN05 = pMaintain.CDT2.isAccessOUT();
      IN25 = pMaintain.CDT2.isAccessDISABLED();
      // Cash discount date 3
      DSP.WFCDT3.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.CDT3.get(), LDAZD.DTFM, ' '));
      IN05 = pMaintain.CDT3.isAccessOUT();
      IN25 = pMaintain.CDT3.isAccessDISABLED();
      // Cash discount percentage 1
      this.PXDCCD = pMaintain.CDP1.getDecimals();
      this.PXFLDD = 2 + this.PXDCCD;
      this.PXEDTC = '4';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDP1.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDP1.moveRight(this.PXALPH);
      IN05 = pMaintain.CDP1.isAccessOUT();
      IN25 = pMaintain.CDP1.isAccessDISABLED();
      // Cash discount percentage 2
      this.PXDCCD = pMaintain.CDP2.getDecimals();
      this.PXFLDD = 2 + this.PXDCCD;
      this.PXEDTC = '4';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDP2.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDP2.moveRight(this.PXALPH);
      IN05 = pMaintain.CDP2.isAccessOUT();
      IN25 = pMaintain.CDP2.isAccessDISABLED();
      // Cash discount percentage 3
      this.PXDCCD = pMaintain.CDP3.getDecimals();
      this.PXFLDD = 2 + this.PXDCCD;
      this.PXEDTC = '4';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDP3.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDP3.moveRight(this.PXALPH);
      IN05 = pMaintain.CDP3.isAccessOUT();
      IN25 = pMaintain.CDP3.isAccessDISABLED();
      // Cash discount base
      this.PXDCCD = pMaintain.TASD.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.TASD.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFTASD.moveRight(this.PXALPH);
      IN05 = pMaintain.TASD.isAccessOUT();
      IN25 = pMaintain.TASD.isAccessDISABLED();
      // Cash discount amount 1
      this.PXDCCD = pMaintain.CDC1.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDC1.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDC1.moveRight(this.PXALPH);
      IN05 = pMaintain.CDC1.isAccessOUT();
      IN25 = pMaintain.CDC1.isAccessDISABLED();
      // Cash discount amount 2
      this.PXDCCD = pMaintain.CDC2.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDC2.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDC2.moveRight(this.PXALPH);
      IN05 = pMaintain.CDC2.isAccessOUT();
      IN25 = pMaintain.CDC2.isAccessDISABLED();
      // Cash discount amount 3
      this.PXDCCD = pMaintain.CDC3.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CDC3.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFCDC3.moveRight(this.PXALPH);
      IN05 = pMaintain.CDC3.isAccessOUT();
      IN25 = pMaintain.CDC3.isAccessDISABLED();
      // Taxable amount
      this.PXDCCD = pMaintain.TTXA.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.TTXA.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFTTXA.moveRight(this.PXALPH);
      IN05 = pMaintain.TTXA.isAccessOUT();
      IN25 = pMaintain.TTXA.isAccessDISABLED();
      // Prepaid amount
      this.PXDCCD = pMaintain.PRPA.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.PRPA.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WFPRPA.moveRight(this.PXALPH);
      IN05 = pMaintain.PRPA.isAccessOUT();
      IN25 = pMaintain.PRPA.isAccessDISABLED();
      // VAT Registration number
      DSP.WFVRNO.moveLeftPad(pMaintain.VRNO.get());
      IN15 = pMaintain.VRNO.isAccessOUT();
      IN35 = pMaintain.VRNO.isAccessDISABLED();
      // Tax applicable
      DSP.WFTXAP = pMaintain.TXAP.get();
      IN05 = pMaintain.TXAP.isAccessOUT();
      IN25 = pMaintain.TXAP.isAccessDISABLED();
      // Geographical code
      DSP.WFGEOC = pMaintain.GEOC.get();
      IN14 = pMaintain.GEOC.isAccessOUT();
      IN34 = pMaintain.GEOC.isAccessDISABLED();
      // Tax included
      DSP.WFTXIN = pMaintain.TXIN.getInt();
      IN16 = pMaintain.TXIN.isAccessOUT();
      IN36 = pMaintain.TXIN.isAccessDISABLED();
      // Bank accont identity
      DSP.WFBKID.moveLeftPad(pMaintain.BKID.get());
      IN13 = pMaintain.BKID.isAccessOUT();
      IN33 = pMaintain.BKID.isAccessDISABLED();
      // Delivery date
      DSP.WFDEDA.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.DEDA.get(), LDAZD.DTFM, ' '));
      IN09 = pMaintain.DEDA.isAccessOUT();
      IN29 = pMaintain.DEDA.isAccessDISABLED();
      // - DEBIT NOTE -
      // AP std document
      DSP.WFSDAP.moveLeftPad(pMaintain.SDAP.get());
      IN17 = pMaintain.SDAP.isAccessOUT();
      IN37 = pMaintain.SDAP.isAccessDISABLED();
      // Debit note reason
      DSP.WFDNRE.moveLeftPad(pMaintain.DNRE.get());
      IN06 = pMaintain.DNRE.isAccessOUT();
      IN26 = pMaintain.DNRE.isAccessDISABLED();
      // Text line 1
      DSP.WFSDA1.moveLeftPad(pMaintain.SDA1.get());
      IN06 = pMaintain.SDA1.isAccessOUT();
      IN26 = pMaintain.SDA1.isAccessDISABLED();
      // Text line 2
      DSP.WFSDA2.moveLeftPad(pMaintain.SDA2.get());
      IN06 = pMaintain.SDA2.isAccessOUT();
      IN26 = pMaintain.SDA2.isAccessDISABLED();
      // Text line 3
      DSP.WFSDA3.moveLeftPad(pMaintain.SDA3.get());
      IN06 = pMaintain.SDA3.isAccessOUT();
      IN26 = pMaintain.SDA3.isAccessDISABLED();
      // - SELF BILLING INVOICE -
      // Supplier acceptance
      DSP.WFSUAC = pMaintain.SUAC.get();
      IN07 = pMaintain.SUAC.isAccessOUT();
      IN27 = pMaintain.SUAC.isAccessDISABLED();
      // Condition for add line
      DSP.WFSBAD = pMaintain.SBAD.get();
      IN01 = pMaintain.SBAD.isAccessOUT();
      IN21 = pMaintain.SBAD.isAccessDISABLED();
      // Inv/receiving number
      DSP.WFUPBI = pMaintain.UPBI.getInt();
      IN01 = pMaintain.UPBI.isAccessOUT();
      IN21 = pMaintain.UPBI.isAccessDISABLED();
      // Our invoice address
      DSP.WFPYAD.moveLeftPad(pMaintain.PYAD.get());
      IN07 = pMaintain.PYAD.isAccessOUT();
      IN27 = pMaintain.PYAD.isAccessDISABLED();
      // - EDI INVOICE -
      // Document code
      DSP.WFDNCO.moveLeftPad(pMaintain.DNCO.get());
      IN08 = pMaintain.DNCO.isAccessOUT();
      IN28 = pMaintain.DNCO.isAccessDISABLED();
      // EAN code supplier
      DSP.WFEALS.moveLeftPad(pMaintain.EALS.get());
      IN08 = pMaintain.EALS.isAccessOUT();
      IN28 = pMaintain.EALS.isAccessDISABLED();
      // EAN code consignee
      DSP.WFEALR.moveLeftPad(pMaintain.EALR.get());
      IN08 = pMaintain.EALR.isAccessOUT();
      IN28 = pMaintain.EALR.isAccessDISABLED();
      // EAN code payee
      DSP.WFEALP.moveLeftPad(pMaintain.EALP.get());
      IN08 = pMaintain.EALP.isAccessOUT();
      IN28 = pMaintain.EALP.isAccessDISABLED();
      // Original Supplier invoice number
      DSP.WFDNOI.moveLeftPad(pMaintain.DNOI.get());
      IN10 = pMaintain.DNOI.isAccessOUT();
      IN30 = pMaintain.DNOI.isAccessDISABLED();
      // Original invoice year
      DSP.WFOYEA = pMaintain.OYEA.get();
      IN10 = pMaintain.OYEA.isAccessOUT();
      IN30 = pMaintain.OYEA.isAccessDISABLED();
      // Payment reference number
      DSP.WFPPYR.moveLeftPad(pMaintain.PPYR.get());
      IN11 = pMaintain.PPYR.isAccessOUT();
      IN31 = pMaintain.PPYR.isAccessDISABLED();
      // Payment request number
      DSP.WFPPYN.moveLeftPad(pMaintain.PPYN.get());
      IN12 = pMaintain.PPYN.isAccessOUT();
      IN32 = pMaintain.PPYN.isAccessDISABLED();
      // Payment year
      DSP.WFYEA4 = pMaintain.YEA4.get();
      IN12 = pMaintain.YEA4.isAccessOUT();
      IN32 = pMaintain.YEA4.isAccessDISABLED();
      // Entry date
      DSP.WFRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WFLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WFCHID.moveLeftPad(pMaintain.CHID.get());
   }

   /**
   * F-panel - display - roll.
   */
   public void PFDSP_X0E78R() {
      roll(DSP.WFDIVI, DSP.WFINBN, "WFINBN");
   }

   /**
   * F-panel - check.
   */
   public void PFCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PFCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelF = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'F', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
   }

   /**
   * F-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PFCHK_prepare() {
      // Invoice batch type
      if (pMaintain.IBTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IBTP.set().moveLeftPad(DSP.WFIBTP);
      }
      // Supplier invoice number
      if (pMaintain.SINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SINO.set().moveLeftPad(DSP.WFSINO);
      }
      // Cash discount terms
      if (pMaintain.TECD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.TECD.set().moveLeftPad(DSP.WFTECD);
      }
      // Cash discount date 1
      if (pMaintain.CDT1.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WFCDT1.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WFDIVI, DSP.WFCDT1, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WFCDT1");
               // MSGID=WCD1001 Cash discount date 1 &1 is invalid
               COMPMQ("WCD1001", DSP.WFCDT1);
               return false;
            }
            pMaintain.CDT1.set(CRCalendar.getDate());
         }
      }
      // Cash discount date 2
      if (pMaintain.CDT2.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WFCDT2.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WFDIVI, DSP.WFCDT2, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WFCDT2");
               // MSGID=WCD1101 Cash discount date 2 &1 is invalid
               COMPMQ("WCD1101", DSP.WFCDT2);
               return false;
            }
            pMaintain.CDT2.set(CRCalendar.getDate());
         }
      }
      // Cash discount date 3
      if (pMaintain.CDT3.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WFCDT3.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WFDIVI, DSP.WFCDT3, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WFCDT3");
               // MSGID=WCD1201 Cash discount date 3 &1 is invalid
               COMPMQ("WCD1201", DSP.WFCDT3);
               return false;
            }
            pMaintain.CDT3.set(CRCalendar.getDate());
         }
      }
      // Cash discount amount 1
      if (pMaintain.CDC1.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDC1.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDC1);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDC1");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDC1.set(this.PXNUM);
      }
      // Cash discount amount 2
      if (pMaintain.CDC2.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDC2.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDC2);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDC2");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDC2.set(this.PXNUM);
      }
      // Cash discount amount 3
      if (pMaintain.CDC3.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDC3.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDC3);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDC3");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDC3.set(this.PXNUM);
      }
      // Cash discount percentage 1
      if (pMaintain.CDP1.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDP1.getDecimals();
         this.PXFLDD = 2 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDP1);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDP1");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDP1.set(this.PXNUM);
      }
      // Cash discount percentage 2
      if (pMaintain.CDP2.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDP2.getDecimals();
         this.PXFLDD = 2 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDP2);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDP2");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDP2.set(this.PXNUM);
      }
      // Cash discount percentage 3
      if (pMaintain.CDP3.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CDP3.getDecimals();
         this.PXFLDD = 2 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFCDP3);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFCDP3");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CDP3.set(this.PXNUM);
      }
      // Document code
      if (pMaintain.DNCO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.DNCO.set().moveLeftPad(DSP.WFDNCO);
      }
      // Supplier acceptance
      if (pMaintain.SUAC.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUAC.set(DSP.WFSUAC);
      }
      // Conditions for adding lines
      if (pMaintain.SBAD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SBAD.set(DSP.WFSBAD);
      }
      // Invoice per receiving number
      if (pMaintain.UPBI.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.UPBI.set(DSP.WFUPBI);
      }
      // AP standard document
      if (pMaintain.SDAP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDAP.set().moveLeftPad(DSP.WFSDAP);
      }
      // Debit note reason
      if (pMaintain.DNRE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.DNRE.set().moveLeftPad(DSP.WFDNRE);
      }
      // Our invoicing address
      if (pMaintain.PYAD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PYAD.set().moveLeftPad(DSP.WFPYAD);
      }
      // Text line 1
      if (pMaintain.SDA1.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA1.set().moveLeftPad(DSP.WFSDA1);
      }
      // Text line 2
      if (pMaintain.SDA2.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA2.set().moveLeftPad(DSP.WFSDA2);
      }
      // Text line 3
      if (pMaintain.SDA3.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA3.set().moveLeftPad(DSP.WFSDA3);
      }
      // Total taxable amount
      if (pMaintain.TTXA.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.TTXA.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFTTXA);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFTTXA");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.TTXA.set(this.PXNUM);
      }
      // Cash discount base
      if (pMaintain.TASD.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.TASD.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFTASD);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFTASD");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.TASD.set(this.PXNUM);
      }
      // Pre-paid amount
      if (pMaintain.PRPA.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.PRPA.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WFPRPA);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WFPRPA");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.PRPA.set(this.PXNUM);
      }
      // Tax applicable
      if (pMaintain.TXAP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.TXAP.set(DSP.WFTXAP);
      }
      // Geographical code
      if (pMaintain.GEOC.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.GEOC.set(DSP.WFGEOC);
      }
      // Tax included
      if (pMaintain.TXIN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.TXIN.set(DSP.WFTXIN);
      }
      // EAN location code payee
      if (pMaintain.EALP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.EALP.set().moveLeftPad(DSP.WFEALP);
      }
      // EAN location code consignee
      if (pMaintain.EALR.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.EALR.set().moveLeftPad(DSP.WFEALR);
      }
      // EAN location code supplier
      if (pMaintain.EALS.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.EALS.set().moveLeftPad(DSP.WFEALS);
      }
      // VAT reg no
      if (pMaintain.VRNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.VRNO.set().moveLeftPad(DSP.WFVRNO);
      }
      // Bank account identity
      if (pMaintain.BKID.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.BKID.set().moveLeftPad(DSP.WFBKID);
      }
      // Delivery date
      if (pMaintain.DEDA.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WFDEDA.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WFDIVI, DSP.WFDEDA, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WFDEDA");
               // MSGID=WDEDA01 Delivery date &1 is invalid
               COMPMQ("WDEDA01", DSP.WFDEDA);
               return false;
            }
            pMaintain.DEDA.set(CRCalendar.getDate());
         }
      }
      // Original invoice number
      if (pMaintain.DNOI.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.DNOI.set().moveLeftPad(DSP.WFDNOI);
      }
      // Original invoice year
      if (pMaintain.OYEA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.OYEA.set(DSP.WFOYEA);
      }
      // Payment reference number
      if (pMaintain.PPYR.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PPYR.set().moveLeftPad(DSP.WFPPYR);
      }
      // Payment request number
      if (pMaintain.PPYN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PPYN.set().moveLeftPad(DSP.WFPPYN);
      }
      // Payment year
      if (pMaintain.YEA4.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.YEA4.set(DSP.WFYEA4);
      }
      return true;
   }

   /**
   * F-panel - update.
   */
   public void PFUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      preparePanelF = true;
      // =========================================
      // Handle messages
      error = handleMessages(pMaintain.messages, 'F', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         if (CRCommon.getMode() == cEnumMode.ADD) {
            // End of ADD mode
            picSetMethod('I');
            CRCommon.setChangeMode();
            APIBH.setDIVI().moveLeftPad(pMaintain.DIVI.get());
            APIBH.setINBN(pMaintain.INBN.get());
         }
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * F-panel - prompt.
   */
   public boolean PFPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PFPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * F-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PFPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt Cash discount terms
      if (DSP.hasFocus("WFTECD")) {
         found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, false, APIBH.getCONO(), pMaintain.SPYN.get());
         if (cRefTECDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.TECD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WFTECD, IDMAS.getLNCD())) 
         {
            DSP.WFTECD.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Our invoicing adress
      if (DSP.hasFocus("WFPYAD")) {
         IADDR.setADK1().clear();
         IADDR.setADK3().clear();
         if (cRefPYADext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PYAD.isAccessMANDATORYorOPTIONAL(), 
             IADDR, APIBH.getCONO(), 3, IADDR.getADK1(), DSP.WFPYAD, IADDR.getADK3())) 
         {
            DSP.WFPYAD.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Original invoice number
      if (DSP.hasFocus("WFDNOI")) {
         if (cRefDNOIext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.DNOI.isAccessMANDATORYorOPTIONAL(), 
             PLEDG, APIBH.getCONO(), DSP.WFDIVI, pMaintain.SUNO.get(), pMaintain.SPYN.get(), DSP.WFDNOI, DSP.WFOYEA)) 
         {
            DSP.WFDNOI.moveLeftPad(this.PXKVA4);
            DSP.WFOYEA = this.PXKVA5.getIntLeft(cRefOYEA.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Payment request number
      if (DSP.hasFocus("WFPPYN")) {
         if (cRefPPYNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PPYN.isAccessMANDATORYorOPTIONAL(), 
             PPPAY, APIBH.getCONO(), DSP.WFDIVI, pMaintain.SPYN.get(), pMaintain.SUNO.get(), DSP.WFPPYN, DSP.WFPPYR, DSP.WFYEA4)) 
         {
            DSP.WFPPYN.moveLeftPad(this.PXKVA5);
            DSP.WFPPYR.moveLeftPad(this.PXKVA6);
            DSP.WFYEA4 = this.PXKVA7.getIntLeft(cRefYEA4.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Payment reference number
      if (DSP.hasFocus("WFPPYR")) {
         if (cRefPPYRext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PPYR.isAccessMANDATORYorOPTIONAL(), 
             PPPAY, APIBH.getCONO(), DSP.WFDIVI, pMaintain.SPYN.get(), pMaintain.SUNO.get(), DSP.WFPPYR, DSP.WFYEA4, DSP.WFPPYN)) 
         {
            DSP.WFPPYR.moveLeftPad(this.PXKVA5);
            DSP.WFYEA4 = this.PXKVA6.getIntLeft(cRefYEA4.length());
            DSP.WFPPYN.moveLeftPad(this.PXKVA7);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Bank account identity
      if (DSP.hasFocus("WFBKID")) {
         if (!pMaintain.SPYN.get().isBlank()) {
            BANAC.setACHO().moveLeftPad(pMaintain.SPYN.get());
         } else {
            BANAC.setACHO().moveLeftPad(pMaintain.SUNO.get());
         }
         if (cRefBKIDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.BKID.isAccessMANDATORYorOPTIONAL(), 
             BANAC, APIBH.getCONO(), DSP.WFDIVI, 3, BANAC.getACHO(), DSP.WFBKID)) 
         {
            DSP.WFBKID.moveLeftPad(this.PXKVA5);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Geographical code
      if (DSP.hasFocus("WFGEOC")) {
          // - Chain CRS793 - Geographical jurisdiction parameters
         found_CSYPAR_CRS793 = cRefPGNMext.getCSYPAR_PGNM_blankDIVI(SYPAR, found_CSYPAR_CRS793, LDAZD.CONO, "CRS793");
         CRS793DS.set().moveLeft(SYPAR.getPARM());
         if (cRefGEOCext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.GEOC.isAccessMANDATORYorOPTIONAL(), 
             GEOJU, APIBH.getCONO(), DSP.WFGEOC, CRS793DS.getT9TAJ2(), CRS793DS.getT9TAJ3(), CRS793DS.getT9TAJ4(), CRS793DS.getT9TAJ5())) {
            DSP.WFGEOC = this.PXKVA3.getIntLeft(cRefGEOC.length());            
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Debit note reason
      if (DSP.hasFocus("WFDNRE")) {
         if (cRefREAPext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.DNRE.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WFDIVI, DSP.WFDNRE)) 
         {
            DSP.WFDNRE.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_F(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("IBTP")
          || FLDI.EQ("SINO")
          || FLDI.EQ("TECD")
          || FLDI.EQ("CDT1")
          || FLDI.EQ("CDT2")
          || FLDI.EQ("CDT3")
          || FLDI.EQ("CDC1")
          || FLDI.EQ("CDC2")
          || FLDI.EQ("CDC3")
          || FLDI.EQ("CDP1")
          || FLDI.EQ("CDP2")
          || FLDI.EQ("CDP3")
          || FLDI.EQ("TASD")
          || FLDI.EQ("TTXA")
          || FLDI.EQ("PRPA")
          || FLDI.EQ("VRNO")
          || FLDI.EQ("BKID")
          || FLDI.EQ("TXAP")
          || FLDI.EQ("GEOC")
          || FLDI.EQ("TXIN")
          || FLDI.EQ("SDAP")
          || FLDI.EQ("DNRE")
          || FLDI.EQ("SDA1")
          || FLDI.EQ("SDA2")
          || FLDI.EQ("SDA3")
          || FLDI.EQ("SUAC")
          || FLDI.EQ("SBAD")
          || FLDI.EQ("UPBI")
          || FLDI.EQ("PYAD")
          || FLDI.EQ("DNCO")
          || FLDI.EQ("EALP")
          || FLDI.EQ("EALR")
          || FLDI.EQ("EALS")
          || FLDI.EQ("DEDA")
          || FLDI.EQ("DNOI")
          || FLDI.EQ("OYEA")
          || FLDI.EQ("PPYN")
          || FLDI.EQ("PPYR")
          || FLDI.EQ("YEA4")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_F(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WFDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WFINBN");
      } else if (FLDI.EQ("IBTP")) {  DSP.setFocus("WFIBTP");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WFSINO");
      } else if (FLDI.EQ("TECD")) {  DSP.setFocus("WFTECD");
      } else if (FLDI.EQ("CDT1")) {  DSP.setFocus("WFCDT1");
      } else if (FLDI.EQ("CDT2")) {  DSP.setFocus("WFCDT2");
      } else if (FLDI.EQ("CDT3")) {  DSP.setFocus("WFCDT3");
      } else if (FLDI.EQ("CDC1")) {  DSP.setFocus("WFCDC1");
      } else if (FLDI.EQ("CDC2")) {  DSP.setFocus("WFCDC2");
      } else if (FLDI.EQ("CDC3")) {  DSP.setFocus("WFCDC3");
      } else if (FLDI.EQ("CDP1")) {  DSP.setFocus("WFCDP1");
      } else if (FLDI.EQ("CDP2")) {  DSP.setFocus("WFCDP2");
      } else if (FLDI.EQ("CDP3")) {  DSP.setFocus("WFCDP3");
      } else if (FLDI.EQ("TASD")) {  DSP.setFocus("WFTASD");
      } else if (FLDI.EQ("TTXA")) {  DSP.setFocus("WFTTXA");
      } else if (FLDI.EQ("PRPA")) {  DSP.setFocus("WFPRPA");
      } else if (FLDI.EQ("VRNO")) {  DSP.setFocus("WFVRNO");
      } else if (FLDI.EQ("BKID")) {  DSP.setFocus("WFBKID");
      } else if (FLDI.EQ("TXAP")) {  DSP.setFocus("WFTXAP");
      } else if (FLDI.EQ("GEOC")) {  DSP.setFocus("WFGEOC");
      } else if (FLDI.EQ("TXIN")) {  DSP.setFocus("WFTXIN");
      } else if (FLDI.EQ("SDAP")) {  DSP.setFocus("WFSDAP");
      } else if (FLDI.EQ("DNRE")) {  DSP.setFocus("WFDNRE");
      } else if (FLDI.EQ("SDA1")) {  DSP.setFocus("WFSDA1");
      } else if (FLDI.EQ("SDA2")) {  DSP.setFocus("WFSDA2");
      } else if (FLDI.EQ("SDA3")) {  DSP.setFocus("WFSDA3");
      } else if (FLDI.EQ("SUAC")) {  DSP.setFocus("WFSUAC");
      } else if (FLDI.EQ("SBAD")) {  DSP.setFocus("WFSBAD");
      } else if (FLDI.EQ("UPBI")) {  DSP.setFocus("WFUPBI");
      } else if (FLDI.EQ("PYAD")) {  DSP.setFocus("WFPYAD");
      } else if (FLDI.EQ("DNCO")) {  DSP.setFocus("WFDNCO");
      } else if (FLDI.EQ("EALP")) {  DSP.setFocus("WFEALP");
      } else if (FLDI.EQ("EALR")) {  DSP.setFocus("WFEALR");
      } else if (FLDI.EQ("EALS")) {  DSP.setFocus("WFEALS");
      } else if (FLDI.EQ("DEDA")) {  DSP.setFocus("WFDEDA");
      } else if (FLDI.EQ("DNOI")) {  DSP.setFocus("WFDNOI");
      } else if (FLDI.EQ("OYEA")) {  DSP.setFocus("WFOYEA");
      } else if (FLDI.EQ("PPYN")) {  DSP.setFocus("WFPPYN");
      } else if (FLDI.EQ("PPYR")) {  DSP.setFocus("WFPPYR");
      } else if (FLDI.EQ("YEA4")) {  DSP.setFocus("WFYEA4");
      }
   }

   /**
   * G-panel - initiate.
   */
   public void PGINZ() {
      preparePanelG = true;
      // Call APS450Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pMaintain.INBN.set(APIBH.getINBN());
         // - Division
         pMaintain.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pMaintain.APIBH = APIBH;
      pMaintain.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'G', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
   }

   /**
   * G-panel - display - initiate.
   */
   public void PGDSP_INZ() {
      // Set last panel
      lastPanel = 'G';
      // Check if text exists
      IN57 = pMaintain.TXID.get() != 0L;
      // Check if the display fields should be prepared
      if (preparePanelG) {
         preparePanelG = false;
         PGDSP_INZ_prepare();
      }
   }

   /**
   * G-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PGDSP_INZ_prepare() {
      // Division
      DSP.WGDIVI.moveLeftPad(pMaintain.DIVI.get());
      IN02 = pMaintain.DIVI.isAccessOUT();
      IN22 = pMaintain.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WGINBN = pMaintain.INBN.get();
      IN01 = pMaintain.INBN.isAccessOUT();
      IN21 = pMaintain.INBN.isAccessDISABLED();
      // Supplier invoice number
      DSP.WGSINO.moveLeftPad(pMaintain.SINO.get());
      IN03 = pMaintain.SINO.isAccessOUT();
      IN23 = pMaintain.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WGSUPA = pMaintain.SUPA.get();
      IN04 = pMaintain.SUPA.isAccessOUT();
      IN24 = pMaintain.SUPA.isAccessDISABLED();
      // Supplier
      DSP.WGSUNO.moveLeftPad(pMaintain.SUNO.get());
      IN05 = pMaintain.SUNO.isAccessOUT();
      IN25 = pMaintain.SUNO.isAccessDISABLED();
      // Payee
      DSP.WGSPYN.moveLeftPad(pMaintain.SPYN.get());
      IN06 = pMaintain.SPYN.isAccessOUT();
      IN26 = pMaintain.SPYN.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pMaintain.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WGCUAM.moveRight(this.PXALPH);
      IN07 = pMaintain.CUAM.isAccessOUT();
      IN27 = pMaintain.CUAM.isAccessDISABLED();
      // Currency
      DSP.WGCUCD.moveLeftPad(pMaintain.CUCD.get());
      DSP.WGCUC2.moveLeftPad(pMaintain.CUCD.get());
      DSP.WGCUC3.moveLeftPad(pMaintain.CUCD.get());
      IN07 = pMaintain.CUCD.isAccessOUT();
      IN27 = pMaintain.CUCD.isAccessDISABLED();
      // VAT amount
      IN08 = pMaintain.VTAM.isAccessOUT();
      IN28 = pMaintain.VTAM.isAccessDISABLED();
      if (!IN28) { 	
         this.PXDCCD = pMaintain.VTAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = pMaintain.VTAM.get();
         this.PXALPH.clear();
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         DSP.WGVTAM.moveRight(this.PXALPH);
      }
      // Adjusted amount
      this.PXDCCD = pMaintain.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WGADAB.moveRight(this.PXALPH);
      IN09 = pMaintain.ADAB.isAccessOUT();
      IN29 = pMaintain.ADAB.isAccessDISABLED();
      // Approval date
      DSP.WGAAPD.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.AAPD.get(), LDAZD.DTFM, ' '));
      IN10 = pMaintain.AAPD.isAccessOUT();
      IN30 = pMaintain.AAPD.isAccessDISABLED();
      // Credit number
      DSP.WGCRNO.moveLeftPad(pMaintain.CRNO.get());
      IN11 = pMaintain.CRNO.isAccessOUT();
      IN31 = pMaintain.CRNO.isAccessDISABLED();
      // Your reference
      DSP.WGYRE1.moveLeftPad(pMaintain.YRE1.get());
      IN12 = pMaintain.YRE1.isAccessOUT();
      IN32 = pMaintain.YRE1.isAccessDISABLED();
      // Reject reason
      DSP.WGSCRE.moveLeftPad(pMaintain.SCRE.get());
      IN13 = pMaintain.SCRE.isAccessOUT();
      IN33 = pMaintain.SCRE.isAccessDISABLED();
      // Reprint after adjustment
      DSP.WGRPAA = pMaintain.RPAA.getInt();
      IN14 = pMaintain.RPAA.isAccessOUT();
      IN34 = pMaintain.RPAA.isAccessDISABLED();
      // Reject date
      DSP.WGREJD.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.REJD.get(), LDAZD.DTFM, ' '));
      IN15 = pMaintain.REJD.isAccessOUT();
      IN35 = pMaintain.REJD.isAccessDISABLED();
      // Text line 1
      DSP.WGSDA1.moveLeftPad(pMaintain.SDA1.get());
      IN16 = pMaintain.SDA1.isAccessOUT();
      IN36 = pMaintain.SDA1.isAccessDISABLED();
      // Text line 2
      DSP.WGSDA2.moveLeftPad(pMaintain.SDA2.get());
      IN17 = pMaintain.SDA2.isAccessOUT();
      IN37 = pMaintain.SDA2.isAccessDISABLED();
      // Text line 3
      DSP.WGSDA3.moveLeftPad(pMaintain.SDA3.get());
      IN18 = pMaintain.SDA3.isAccessOUT();
      IN38 = pMaintain.SDA3.isAccessDISABLED();
      // Entry date
      DSP.WGRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WGLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WGCHID.moveLeftPad(pMaintain.CHID.get());
   }

   /**
   * G-panel - display - roll.
   */
   public void PGDSP_X0E78R() {
      roll(DSP.WGDIVI, DSP.WGINBN, "WGINBN");
   }

  /**
   * G-panel - check.
   */
   public void PGCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PGCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelG = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'G', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
   }

   /**
   * G-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PGCHK_prepare() {
      // Supplier invoice number
      if (pMaintain.SINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SINO.set().moveLeftPad(DSP.WGSINO);
      }
      // Supplier accepted
      if (pMaintain.SUPA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUPA.set(DSP.WGSUPA);
      }
      // Supplier
      if (pMaintain.SUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUNO.set().moveLeftPad(DSP.WGSUNO);
      }
      // Payee
      if (pMaintain.SPYN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SPYN.set().moveLeftPad(DSP.WGSPYN);
      }
      // Currency amount
      if (pMaintain.CUAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.CUAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WGCUAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WGCUAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.CUAM.set(this.PXNUM);
      }
      // Currency
      if (pMaintain.CUCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CUCD.set().moveLeftPad(DSP.WGCUCD);
      }
      // VAT amount
      if (pMaintain.VTAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WGVTAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WGVTAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTAM.set(this.PXNUM);
      }
      // Adjusted amount
      if (pMaintain.ADAB.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.ADAB.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WGADAB);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WGADAB");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTAM.set(this.PXNUM);
      }
      // Approval date
      if (pMaintain.AAPD.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WGAAPD.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WGDIVI, DSP.WGAAPD, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WGAAPD");
               // MSGID=WAA2301 Approval date &1 is invalid
               COMPMQ("WAA2301", DSP.WGAAPD);
               return false;
            }
            pMaintain.AAPD.set(CRCalendar.getDate());
         }
      }
      // Credit number
      if (pMaintain.CRNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CRNO.set().moveLeftPad(DSP.WGCRNO);
      }
      // Your reference
      if (pMaintain.YRE1.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.YRE1.set().moveLeftPad(DSP.WGYRE1);
      }
      // Reject reason
      if (pMaintain.SCRE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SCRE.set().moveLeftPad(DSP.WGSCRE);
      }
      // Reprint after adjustment
      if (pMaintain.RPAA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.RPAA.set(DSP.WGRPAA);
      }
      // Rejection date
      if (pMaintain.REJD.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WGREJD.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WGDIVI, DSP.WGREJD, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WGREJD");
               // MSGID=WRE5301 Reject date &1 is invalid
               COMPMQ("WRE5301", DSP.WGREJD);
               return false;
            }
            pMaintain.REJD.set(CRCalendar.getDate());
         }
      }
      // Text line 1
      if (pMaintain.SDA1.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA1.set().moveLeftPad(DSP.WGSDA1);
      }
      // Text line 2
      if (pMaintain.SDA2.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA2.set().moveLeftPad(DSP.WGSDA2);
      }
      // Text line 3
      if (pMaintain.SDA3.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA3.set().moveLeftPad(DSP.WGSDA3);
      }
      return true;
   }

   /**
   * G-panel - update.
   */
   public void PGUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      preparePanelG = true;
      // =========================================
      // Handle messages
      error = handleMessages(pMaintain.messages, 'G', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * G-panel - prompt.
   */
   public boolean PGPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PGPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * G-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PGPMT_perform() {
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_G(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("SPYN")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("AAPD")
          || FLDI.EQ("CRNO")
          || FLDI.EQ("YRE1")
          || FLDI.EQ("SCRE")
          || FLDI.EQ("RPAA")
          || FLDI.EQ("REJD")
          || FLDI.EQ("SDA1")
          || FLDI.EQ("SDA2")
          || FLDI.EQ("SDA3")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_G(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WGDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WGINBN");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WGSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WGSUPA");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WGSUNO");
      } else if (FLDI.EQ("SPYN")) {  DSP.setFocus("WGSPYN");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WGCUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WGCUCD");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WGVTAM");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WGADAB");
      } else if (FLDI.EQ("AAPD")) {  DSP.setFocus("WGAAPD");
      } else if (FLDI.EQ("CRNO")) {  DSP.setFocus("WGCRNO");
      } else if (FLDI.EQ("YRE1")) {  DSP.setFocus("WGYRE1");
      } else if (FLDI.EQ("SCRE")) {  DSP.setFocus("WGSCRE");
      } else if (FLDI.EQ("RPAA")) {  DSP.setFocus("WGRPAA");
      } else if (FLDI.EQ("REJD")) {  DSP.setFocus("WGREJD");
      } else if (FLDI.EQ("SDA1")) {  DSP.setFocus("WGSDA1");
      } else if (FLDI.EQ("SDA2")) {  DSP.setFocus("WGSDA2");
      } else if (FLDI.EQ("SDA3")) {  DSP.setFocus("WGSDA3");
      }
   }

   /**
   * K-panel - initiate.
   */
   public void PKINZ() {
      preparePanelK = true;
      // Call APS450Fnc, change division - step initiate
      // =========================================
      pChangeDivision = get_pChangeDivision();
      pChangeDivision.messages.forgetNotifications();
      pChangeDivision.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pChangeDivision.INBN.set(APIBH.getINBN());
         // - Division
         pChangeDivision.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pChangeDivision.APIBH = APIBH;
      pChangeDivision.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pChangeDivision);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pChangeDivision.messages, 'K', false);
      // Release resources allocated by the parameter list.
      pChangeDivision.release();
   }

   /**
   * K-panel - display - initiate.
   */
   public void PKDSP_INZ() {
      // Set last panel
      lastPanel = 'K';
      // Check if the display fields should be prepared
      if (preparePanelK) {
         preparePanelK = false;
         PKDSP_INZ_prepare();
      }
   }

   /**
   * K-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PKDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WKINBN = pChangeDivision.INBN.get();
      IN01 = pChangeDivision.INBN.isAccessOUT();
      IN21 = pChangeDivision.INBN.isAccessDISABLED();
      // Division
      DSP.WKDIVI.moveLeftPad(pChangeDivision.DIVI.get());
      IN02 = pChangeDivision.DIVI.isAccessOUT();
      IN22 = pChangeDivision.DIVI.isAccessDISABLED();
      // Supplier invoice number
      DSP.WKSINO.moveLeftPad(pChangeDivision.SINO.get());
      IN03 = pChangeDivision.SINO.isAccessOUT();
      IN23 = pChangeDivision.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WKSUPA = pChangeDivision.SUPA.get();
      IN04 = pChangeDivision.SUPA.isAccessOUT();
      IN24 = pChangeDivision.SUPA.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pChangeDivision.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pChangeDivision.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WKCUAM.moveRight(this.PXALPH);
      IN07 = pChangeDivision.CUAM.isAccessOUT();
      IN27 = pChangeDivision.CUAM.isAccessDISABLED();
      // Currency
      DSP.WKCUCD.moveLeftPad(pChangeDivision.CUCD.get());
      IN07 = pChangeDivision.CUCD.isAccessOUT();
      IN27 = pChangeDivision.CUCD.isAccessDISABLED();
      // Supplier
      DSP.WKSUNO.moveLeftPad(pChangeDivision.SUNO.get());
      IN05 = pChangeDivision.SUNO.isAccessOUT();
      IN25 = pChangeDivision.SUNO.isAccessDISABLED();
      // Payee
      DSP.WKSPYN.moveLeftPad(pChangeDivision.SUNO.get());
      IN06 = pChangeDivision.SUNO.isAccessOUT();
      IN26 = pChangeDivision.SUNO.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pChangeDivision.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pChangeDivision.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WKADAB.moveRight(this.PXALPH);
      IN09 = pChangeDivision.ADAB.isAccessOUT();
      IN29 = pChangeDivision.ADAB.isAccessDISABLED();
      // Currency
      DSP.WKCUC2.moveLeftPad(pChangeDivision.CUCD.get());
      // VAT amount
      this.PXDCCD = pChangeDivision.VTAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pChangeDivision.VTAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WKVTAM.moveRight(this.PXALPH);
      IN08 = pChangeDivision.VTAM.isAccessOUT();
      IN28 = pChangeDivision.VTAM.isAccessDISABLED();
      // Currency
      DSP.WKCUC3.moveLeftPad(pChangeDivision.CUCD.get());
      // New division
      DSP.WKNDIV.moveLeftPad(pChangeDivision.toDIVI.get());
      IN10 = pChangeDivision.toDIVI.isAccessOUT();
      IN30 = pChangeDivision.toDIVI.isAccessDISABLED();
      // Entry date
      DSP.WKRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pChangeDivision.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WKLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pChangeDivision.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WKCHID.moveLeftPad(pChangeDivision.CHID.get());
   }

   /**
   * K-panel - check.
   */
   public void PKCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, change division - step validate
      // =========================================
      pChangeDivision.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PKCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pChangeDivision);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelK = true;
      // =========================================
      // Handle messages
      handleMessages(pChangeDivision.messages, 'K', false);
      if (pChangeDivision.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pChangeDivision.release();
   }

   /**
   * K-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PKCHK_prepare() {
      // New division
      if (pChangeDivision.toDIVI.isAccessMANDATORYorOPTIONAL()) {
         pChangeDivision.toDIVI.set().moveLeftPad(DSP.WKNDIV);
      }
      return true;
   }

   /**
   * K-panel - update.
   */
   public void PKUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, change division - step update
      // =========================================
      pChangeDivision.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pChangeDivision.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pChangeDivision.messages, transStatus);
      preparePanelK = true;
      // =========================================
      // Handle messages
      error = handleMessages(pChangeDivision.messages, 'K', false);
      // Release resources allocated by the parameter list.
      pChangeDivision.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * K-panel - prompt.
   */
   public boolean PKPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PKPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * K-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PKPMT_perform() {
      // ----------------------------------------------------------------
      // New division
      if (DSP.hasFocus("WKNDIV")) {
         if (cRefDIVIext.prompt(this, /*maintain*/ true, /*select*/ pChangeDivision.toDIVI.isAccessMANDATORYorOPTIONAL(), 
             MNDIV, APIBH.getCONO(), DSP.WKNDIV)) 
         {
            DSP.WKNDIV.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_K(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("SPYN")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("toDIVI")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_K(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WKDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WKINBN");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WKSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WKSUPA");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WKSUNO");
      } else if (FLDI.EQ("SPYN")) {  DSP.setFocus("WKSPYN");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WKCUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WKCUCD");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WKADAB");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WKVTAM");
      } else if (FLDI.EQ("toDIVI")) {  DSP.setFocus("WKNDIV");
      }
   }

   /**
   * L-panel - initiate.
   */
   public void PLINZ() {
      preparePanelL = true;
      // Call APS450Fnc, acknowledge - step initiate
      // =========================================
      pAcknowledge = get_pAcknowledge();
      pAcknowledge.messages.forgetNotifications();
      pAcknowledge.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pAcknowledge.INBN.set(APIBH.getINBN());
         // - Division
         pAcknowledge.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pAcknowledge.APIBH = APIBH;
      pAcknowledge.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAcknowledge);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pAcknowledge.messages, 'L', false);
      // Release resources allocated by the parameter list.
      pAcknowledge.release();
   }

   /**
   * L-panel - display - initiate.
   */
   public void PLDSP_INZ() {
      // Set last panel
      lastPanel = 'L';
      // Check if the display fields should be prepared
      if (preparePanelL) {
         preparePanelL = false;
         PLDSP_INZ_prepare();
      }
   }

   /**
   * L-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PLDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WLINBN = pAcknowledge.INBN.get();
      IN01 = pAcknowledge.INBN.isAccessOUT();
      IN21 = pAcknowledge.INBN.isAccessDISABLED();
      // Division
      DSP.WLDIVI.moveLeftPad(pAcknowledge.DIVI.get());
      IN02 = pAcknowledge.DIVI.isAccessOUT();
      IN22 = pAcknowledge.DIVI.isAccessDISABLED();
      // Supplier invoice number
      DSP.WLSINO.moveLeftPad(pAcknowledge.SINO.get());
      IN03 = pAcknowledge.SINO.isAccessOUT();
      IN23 = pAcknowledge.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WLSUPA = pAcknowledge.SUPA.get();
      IN04 = pAcknowledge.SUPA.isAccessOUT();
      IN24 = pAcknowledge.SUPA.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pAcknowledge.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pAcknowledge.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WLCUAM.moveRight(this.PXALPH);
      IN05 = pAcknowledge.CUAM.isAccessOUT();
      IN25 = pAcknowledge.CUAM.isAccessDISABLED();
      // Currency
      DSP.WLCUCD.moveLeftPad(pAcknowledge.CUCD.get());
      IN05 = pAcknowledge.CUCD.isAccessOUT();
      IN25 = pAcknowledge.CUCD.isAccessDISABLED();
      // Supplier
      DSP.WLSUNO.moveLeftPad(pAcknowledge.SUNO.get());
      IN06 = pAcknowledge.SUNO.isAccessOUT();
      IN26 = pAcknowledge.SUNO.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pAcknowledge.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pAcknowledge.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WLADAB.moveRight(this.PXALPH);
      IN07 = pAcknowledge.ADAB.isAccessOUT();
      IN27 = pAcknowledge.ADAB.isAccessDISABLED();
      // Currency
      DSP.WLCUC2.moveLeftPad(pAcknowledge.CUCD.get());
      // VAT amount
      this.PXDCCD = pAcknowledge.VTAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pAcknowledge.VTAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WLVTAM.moveRight(this.PXALPH);
      IN08 = pAcknowledge.VTAM.isAccessOUT();
      IN28 = pAcknowledge.VTAM.isAccessDISABLED();
      // Currency
      DSP.WLCUC3.moveLeftPad(pAcknowledge.CUCD.get());
      // Credit number
      DSP.WLCRNO.moveLeftPad(pAcknowledge.CRNO.get());
      IN10 = pAcknowledge.CRNO.isAccessOUT();
      IN30 = pAcknowledge.CRNO.isAccessDISABLED();
      // Your reference
      DSP.WLYRE1.moveLeftPad(pAcknowledge.YRE1.get());
      IN11 = pAcknowledge.YRE1.isAccessOUT();
      IN31 = pAcknowledge.YRE1.isAccessDISABLED();
      // Entry date
      DSP.WLRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pAcknowledge.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WLLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pAcknowledge.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WLCHID.moveLeftPad(pAcknowledge.CHID.get());
   }

   /**
   * L-panel - check.
   */
   public void PLCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, acknowledge - step validate
      // =========================================
      pAcknowledge.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PLCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAcknowledge);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelL = true;
      // =========================================
      // Handle messages
      handleMessages(pAcknowledge.messages, 'L', false);
      if (pAcknowledge.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pAcknowledge.release();
   }

   /**
   * L-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PLCHK_prepare() {
      // Credit number
      if (pAcknowledge.CRNO.isAccessMANDATORYorOPTIONAL()) {
         pAcknowledge.CRNO.set().moveLeftPad(DSP.WLCRNO);
      }
      // Your reference
      if (pAcknowledge.YRE1.isAccessMANDATORYorOPTIONAL()) {
         pAcknowledge.YRE1.set().moveLeftPad(DSP.WLYRE1);
      }
      return true;
   }

   /**
   * L-panel - update.
   */
   public void PLUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, acknowledge - step update
      // =========================================
      pAcknowledge.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAcknowledge.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAcknowledge.messages, transStatus);
      preparePanelL = true;
      // =========================================
      // Handle messages
      error = handleMessages(pAcknowledge.messages, 'L', false);
      // Release resources allocated by the parameter list.
      pAcknowledge.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * L-panel - prompt.
   */
   public boolean PLPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PLPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * L-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PLPMT_perform() {
      // ----------------------------------------------------------------
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_L(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("CRNO")
          || FLDI.EQ("YRE1")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_L(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WLDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WLINBN");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WLSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WLSUPA");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WLSUNO");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WLCUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WLCUCD");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WLADAB");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WLVTAM");
      } else if (FLDI.EQ("CRNO")) {  DSP.setFocus("WLCRNO");
      } else if (FLDI.EQ("YRE1")) {  DSP.setFocus("WLYRE1");
      }
   }

   /**
   * N-panel - initiate.
   */
   public void PNINZ() {
      preparePanelN = true;
      // Call APS450Fnc, reject - step initiate
      // =========================================
      pRejectInvoice = get_pRejectInvoice();
      pRejectInvoice.messages.forgetNotifications();
      pRejectInvoice.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pRejectInvoice.INBN.set(APIBH.getINBN());
         // - Division
         pRejectInvoice.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pRejectInvoice.APIBH = APIBH;
      pRejectInvoice.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pRejectInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pRejectInvoice.messages, 'N', false);
      // Release resources allocated by the parameter list.
      pRejectInvoice.release();
   }

   /**
   * N-panel - display - initiate.
   */
   public void PNDSP_INZ() {
      // Set last panel
      lastPanel = 'N';
      // Check if the display fields should be prepared
      if (preparePanelN) {
         preparePanelN = false;
         PNDSP_INZ_prepare();
      }
   }

   /**
   * N-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PNDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WNINBN = pRejectInvoice.INBN.get();
      IN01 = pRejectInvoice.INBN.isAccessOUT();
      IN21 = pRejectInvoice.INBN.isAccessDISABLED();
      // Division
      DSP.WNDIVI.moveLeftPad(pRejectInvoice.DIVI.get());
      IN02 = pRejectInvoice.DIVI.isAccessOUT();
      IN22 = pRejectInvoice.DIVI.isAccessDISABLED();
      // Supplier invoice number
      DSP.WNSINO.moveLeftPad(pRejectInvoice.SINO.get());
      IN03 = pRejectInvoice.SINO.isAccessOUT();
      IN23 = pRejectInvoice.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WNSUPA = pRejectInvoice.SUPA.get();
      IN04 = pRejectInvoice.SUPA.isAccessOUT();
      IN24 = pRejectInvoice.SUPA.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pRejectInvoice.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pRejectInvoice.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WNCUAM.moveRight(this.PXALPH);
      IN05 = pRejectInvoice.CUAM.isAccessOUT();
      IN25 = pRejectInvoice.CUAM.isAccessDISABLED();
      // Currency
      DSP.WNCUCD.moveLeftPad(pRejectInvoice.CUCD.get());
      IN05 = pRejectInvoice.CUCD.isAccessOUT();
      IN25 = pRejectInvoice.CUCD.isAccessDISABLED();
      // Supplier
      DSP.WNSUNO.moveLeftPad(pRejectInvoice.SUNO.get());
      IN06 = pRejectInvoice.SUNO.isAccessOUT();
      IN26 = pRejectInvoice.SUNO.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pRejectInvoice.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pRejectInvoice.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WNADAB.moveRight(this.PXALPH);
      IN07 = pRejectInvoice.ADAB.isAccessOUT();
      IN27 = pRejectInvoice.ADAB.isAccessDISABLED();
      // Currency
      DSP.WNCUC2.moveLeftPad(pRejectInvoice.CUCD.get());
      // VAT amount
      this.PXDCCD = pRejectInvoice.VTAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pRejectInvoice.VTAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WNVTAM.moveRight(this.PXALPH);
      IN08 = pRejectInvoice.VTAM.isAccessOUT();
      IN28 = pRejectInvoice.VTAM.isAccessDISABLED();
      // Currency
      DSP.WNCUC3.moveLeftPad(pRejectInvoice.CUCD.get());
      // Reject reason
      DSP.WNSCRE.moveLeftPad(pRejectInvoice.SCRE.get());
      IN09 = pRejectInvoice.SCRE.isAccessOUT();
      IN29 = pRejectInvoice.SCRE.isAccessDISABLED();
      // Reprint after adjustment
      DSP.WNRPAA = pRejectInvoice.RPAA.getInt();
      IN10 = pRejectInvoice.RPAA.isAccessOUT();
      IN30 = pRejectInvoice.RPAA.isAccessDISABLED();
      // Reject date
      DSP.WNREJD.moveLeft(CRCalendar.convertDate(pRejectInvoice.DIVI.get(), pRejectInvoice.REJD.get(), LDAZD.DTFM, ' '));
      IN11 = pRejectInvoice.REJD.isAccessOUT();
      IN31 = pRejectInvoice.REJD.isAccessDISABLED();
      // Text line 1
      DSP.WNSDA1.moveLeftPad(pRejectInvoice.SDA1.get());
      IN12 = pRejectInvoice.SDA1.isAccessOUT();
      IN32 = pRejectInvoice.SDA1.isAccessDISABLED();
      // Text line 2
      DSP.WNSDA2.moveLeftPad(pRejectInvoice.SDA2.get());
      IN13 = pRejectInvoice.SDA2.isAccessOUT();
      IN33 = pRejectInvoice.SDA2.isAccessDISABLED();
      // Text line 3
      DSP.WNSDA3.moveLeftPad(pRejectInvoice.SDA3.get());
      IN14 = pRejectInvoice.SDA3.isAccessOUT();
      IN34 = pRejectInvoice.SDA3.isAccessDISABLED();
      // Entry date
      DSP.WNRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pRejectInvoice.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WNLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pRejectInvoice.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WNCHID.moveLeftPad(pRejectInvoice.CHID.get());
   }

   /**
   * N-panel - check.
   */
   public void PNCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, reject - step validate
      // =========================================
      pRejectInvoice.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PNCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pRejectInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelN = true;
      // =========================================
      // Handle messages
      handleMessages(pRejectInvoice.messages, 'N', false);
      if (pRejectInvoice.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pRejectInvoice.release();
   }

   /**
   * N-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PNCHK_prepare() {
      // Reject reason
      if (pRejectInvoice.SCRE.isAccessMANDATORYorOPTIONAL()) {
         pRejectInvoice.SCRE.set().moveLeftPad(DSP.WNSCRE);
      }
      // Reprint after adjustment
      if (pRejectInvoice.RPAA.isAccessMANDATORYorOPTIONAL()) {
         pRejectInvoice.RPAA.set(DSP.WNRPAA);
      }
      // Reject date
      if (pRejectInvoice.REJD.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WNREJD.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WNDIVI, DSP.WNREJD, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WNREJD");
               // MSGID=WRE5301 Reject date &1 is invalid
               COMPMQ("WRE5301", DSP.WNREJD);
               return false;
            }
            pRejectInvoice.REJD.set(CRCalendar.getDate());
         }
      }
      // Text line 1
      if (pRejectInvoice.SDA1.isAccessMANDATORYorOPTIONAL()) {
         pRejectInvoice.SDA1.set().moveLeftPad(DSP.WNSDA1);
      }
      // Text line 2
      if (pRejectInvoice.SDA2.isAccessMANDATORYorOPTIONAL()) {
         pRejectInvoice.SDA2.set().moveLeftPad(DSP.WNSDA2);
      }
      // Text line 3
      if (pRejectInvoice.SDA3.isAccessMANDATORYorOPTIONAL()) {
         pRejectInvoice.SDA3.set().moveLeftPad(DSP.WNSDA3);
      }
      return true;
   }

   /**
   * N-panel - update.
   */
   public void PNUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, reject - step update
      // =========================================
      pRejectInvoice.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pRejectInvoice.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pRejectInvoice.messages, transStatus);
      preparePanelN = true;
      // =========================================
      // Handle messages
      error = handleMessages(pRejectInvoice.messages, 'N', false);
      // Release resources allocated by the parameter list.
      pRejectInvoice.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * N-panel - prompt.
   */
   public boolean PNPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PNPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * N-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PNPMT_perform() {
      // ----------------------------------------------------------------
      // Reject reason
      if (DSP.hasFocus("WNSCRE")) {
         if (cRefSCREext.prompt(this, /*maintain*/ true, /*select*/ pRejectInvoice.SCRE.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBH.getCONO(), DSP.WNSCRE)) 
         {
            DSP.WNSCRE.moveLeftPad(this.PXKVA4);
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_N(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("SCRE")
          || FLDI.EQ("RPAA")
          || FLDI.EQ("REJD")
          || FLDI.EQ("SDA1")
          || FLDI.EQ("SDA2")
          || FLDI.EQ("SDA3")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_N(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WNDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WNINBN");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WNSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WNSUPA");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WNSUNO");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WNCUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WNCUCD");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WNADAB");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WNVTAM");
      } else if (FLDI.EQ("SCRE")) {  DSP.setFocus("WNSCRE");
      } else if (FLDI.EQ("RPAA")) {  DSP.setFocus("WNRPAA");
      } else if (FLDI.EQ("REJD")) {  DSP.setFocus("WNREJD");
      } else if (FLDI.EQ("SDA1")) {  DSP.setFocus("WNSDA1");
      } else if (FLDI.EQ("SDA2")) {  DSP.setFocus("WNSDA2");
      } else if (FLDI.EQ("SDA3")) {  DSP.setFocus("WNSDA3");
      }
   }

   /**
   * M-panel - initiate.
   */
   public void PMINZ() {
      preparePanelM = true;
      // Call APS450Fnc, approve - step initiate
      // =========================================
      pApproveInvoice = get_pApproveInvoice();
      pApproveInvoice.messages.forgetNotifications();
      pApproveInvoice.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBH) {
         // Set primary keys
         // - Invoice batch number
         pApproveInvoice.INBN.set(APIBH.getINBN());
         // - Division
         pApproveInvoice.DIVI.set().moveLeftPad(APIBH.getDIVI());
      }
      // Pass a reference to the record.
      pApproveInvoice.APIBH = APIBH;
      pApproveInvoice.passFAPIBH = passFAPIBH;
      passFAPIBH = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pApproveInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pApproveInvoice.messages, 'M', false);
      // Release resources allocated by the parameter list.
      pApproveInvoice.release();
   }

   /**
   * M-panel - display - initiate.
   */
   public void PMDSP_INZ() {
      // Set last panel
      lastPanel = 'M';
      // Check if the display fields should be prepared
      if (preparePanelM) {
         preparePanelM = false;
         PMDSP_INZ_prepare();
      }
   }

   /**
   * M-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PMDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WMINBN = pApproveInvoice.INBN.get();
      IN01 = pApproveInvoice.INBN.isAccessOUT();
      IN21 = pApproveInvoice.INBN.isAccessDISABLED();
      // Division
      DSP.WMDIVI.moveLeftPad(pApproveInvoice.DIVI.get());
      IN02 = pApproveInvoice.DIVI.isAccessOUT();
      IN22 = pApproveInvoice.DIVI.isAccessDISABLED();
      // Supplier invoice number
      DSP.WMSINO.moveLeftPad(pApproveInvoice.SINO.get());
      IN03 = pApproveInvoice.SINO.isAccessOUT();
      IN23 = pApproveInvoice.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WMSUPA = pApproveInvoice.SUPA.get();
      IN04 = pApproveInvoice.SUPA.isAccessOUT();
      IN24 = pApproveInvoice.SUPA.isAccessDISABLED();
      // Currency amount
      this.PXDCCD = pApproveInvoice.CUAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pApproveInvoice.CUAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WMCUAM.moveRight(this.PXALPH);
      IN05 = pApproveInvoice.CUAM.isAccessOUT();
      IN25 = pApproveInvoice.CUAM.isAccessDISABLED();
      // Currency
      DSP.WMCUCD.moveLeftPad(pApproveInvoice.CUCD.get());
      IN05 = pApproveInvoice.CUCD.isAccessOUT();
      IN25 = pApproveInvoice.CUCD.isAccessDISABLED();
      // Supplier
      DSP.WMSUNO.moveLeftPad(pApproveInvoice.SUNO.get());
      IN06 = pApproveInvoice.SUNO.isAccessOUT();
      IN26 = pApproveInvoice.SUNO.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pApproveInvoice.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pApproveInvoice.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WMADAB.moveRight(this.PXALPH);
      IN07 = pApproveInvoice.ADAB.isAccessOUT();
      IN27 = pApproveInvoice.ADAB.isAccessDISABLED();
      // Currency
      DSP.WMCUC2.moveLeftPad(pApproveInvoice.CUCD.get());
      // VAT amount
      this.PXDCCD = pApproveInvoice.VTAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pApproveInvoice.VTAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WMVTAM.moveRight(this.PXALPH);
      IN08 = pApproveInvoice.VTAM.isAccessOUT();
      IN28 = pApproveInvoice.VTAM.isAccessDISABLED();
      // Currency
      DSP.WMCUC3.moveLeftPad(pApproveInvoice.CUCD.get());
      // Approval date
      DSP.WMAAPD.moveLeft(CRCalendar.convertDate(pApproveInvoice.DIVI.get(), pApproveInvoice.AAPD.get(), LDAZD.DTFM, ' '));
      IN09 = pApproveInvoice.AAPD.isAccessOUT();
      IN29 = pApproveInvoice.AAPD.isAccessDISABLED();
      // Credit number
      DSP.WMCRNO.moveLeftPad(pApproveInvoice.CRNO.get());
      IN10 = pApproveInvoice.CRNO.isAccessOUT();
      IN30 = pApproveInvoice.CRNO.isAccessDISABLED();
      // Your reference
      DSP.WMYRE1.moveLeftPad(pApproveInvoice.YRE1.get());
      IN11 = pApproveInvoice.YRE1.isAccessOUT();
      IN31 = pApproveInvoice.YRE1.isAccessDISABLED();
      // Invoice date
      DSP.WMIVDT.moveLeft(CRCalendar.convertDate(pApproveInvoice.DIVI.get(), pApproveInvoice.IVDT.get(), LDAZD.DTFM, ' '));
      IN12 = pApproveInvoice.IVDT.isAccessOUT();
      IN32 = pApproveInvoice.IVDT.isAccessDISABLED();
      // Due date
      DSP.WMDUDT.moveLeft(CRCalendar.convertDate(pApproveInvoice.DIVI.get(), pApproveInvoice.DUDT.get(), LDAZD.DTFM, ' '));
      IN13 = pApproveInvoice.DUDT.isAccessOUT();
      IN33 = pApproveInvoice.DUDT.isAccessDISABLED();
      // Entry date
      DSP.WMRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pApproveInvoice.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WMLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pApproveInvoice.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WMCHID.moveLeftPad(pApproveInvoice.CHID.get());
   }

   /**
   * M-panel - check.
   */
   public void PMCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, Approve - step validate
      // =========================================
      pApproveInvoice.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PMCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pApproveInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelM = true;
      // =========================================
      // Handle messages
      handleMessages(pApproveInvoice.messages, 'M', false);
      if (pApproveInvoice.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pApproveInvoice.release();
   }

   /**
   * M-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PMCHK_prepare() {
      // Supplier invoice number
      if (pApproveInvoice.SINO.isAccessMANDATORYorOPTIONAL()) {
         pApproveInvoice.SINO.set().moveLeftPad(DSP.WMSINO);
      }
      // Approval date
      if (pApproveInvoice.AAPD.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WMAAPD.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WMDIVI, DSP.WMAAPD, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WMAAPD");
               // MSGID=WAA2301 Approval date &1 is invalid
               COMPMQ("WAA2301", DSP.WMAAPD);
               return false;
            }
            pApproveInvoice.AAPD.set(CRCalendar.getDate());
         }
      }
      // Credit number
      if (pApproveInvoice.CRNO.isAccessMANDATORYorOPTIONAL()) {
         pApproveInvoice.CRNO.set().moveLeftPad(DSP.WMCRNO);
      }
      // Your reference
      if (pApproveInvoice.YRE1.isAccessMANDATORYorOPTIONAL()) {
         pApproveInvoice.YRE1.set().moveLeftPad(DSP.WMYRE1);
      }
      // Invoice date
      if (pApproveInvoice.IVDT.isAccessMANDATORYorOPTIONAL()) {
         if (!CRCalendar.convertDate(DSP.WMDIVI, DSP.WMIVDT, LDAZD.DTFM)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WMIVDT");
            // MSGID=WIVD101 Invoice date &1 is invalid
            COMPMQ("WIVD101", DSP.WMIVDT);
            return false;
         }
         pApproveInvoice.IVDT.set(CRCalendar.getDate());
      }
      // Due date
      if (pApproveInvoice.DUDT.isAccessMANDATORYorOPTIONAL()) {
         if (DSP.WMDUDT.isBlank()) {
            pApproveInvoice.DUDT.clearValue();
         } else {
            if (!CRCalendar.convertDate(DSP.WMDIVI, DSP.WMDUDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WMDUDT");
               // MSGID=WDUD101 Due date &1 is invalid
               COMPMQ("WDUD101", DSP.WMDUDT);
               return false;
            }
            pApproveInvoice.DUDT.set(CRCalendar.getDate());
         }
      }
      return true;
   }

   /**
   * M-panel - update.
   */
   public void PMUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, approve - step update
      // =========================================
      pApproveInvoice.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pApproveInvoice.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pApproveInvoice.messages, transStatus);
      preparePanelM = true;
      // =========================================
      // Handle messages
      error = handleMessages(pApproveInvoice.messages, 'M', false);
      // Release resources allocated by the parameter list.
      pApproveInvoice.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * M-panel - prompt.
   */
   public boolean PMPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PMPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * M-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PMPMT_perform() {
      // ----------------------------------------------------------------
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_M(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SUNO")
          || FLDI.EQ("CUAM")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("VTAM")
          || FLDI.EQ("AAPD")
          || FLDI.EQ("CRNO")
          || FLDI.EQ("YRE1")
          || FLDI.EQ("IVDT")
          || FLDI.EQ("DUDT")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_M(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WMDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WMINBN");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WMSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WMSUPA");
      } else if (FLDI.EQ("SUNO")) {  DSP.setFocus("WMSUNO");
      } else if (FLDI.EQ("CUAM")) {  DSP.setFocus("WMCUAM");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WMCUCD");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WMADAB");
      } else if (FLDI.EQ("VTAM")) {  DSP.setFocus("WMVTAM");
      } else if (FLDI.EQ("AAPD")) {  DSP.setFocus("WMAAPD");
      } else if (FLDI.EQ("CRNO")) {  DSP.setFocus("WMCRNO");
      } else if (FLDI.EQ("YRE1")) {  DSP.setFocus("WMYRE1");
      } else if (FLDI.EQ("IVDT")) {  DSP.setFocus("WMIVDT");
      } else if (FLDI.EQ("DUDT")) {  DSP.setFocus("WMDUDT");
      }
   }

   /**
   * P-panel - initiate.
   */
   public void PPINZ() {
      preparePanelP = true;
      // Call APS450Fnc, settings - step initiate
      // =========================================
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      // Set key parameters
      // - Responsible
      pSettings.RESP.set().move(LDAZD.RESP);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
   }

   /**
   * P-panel - display - initiate.
   */
   public void PPDSP_INZ() {
      // Set last panel
      lastPanel = 'P';
      // Check if the display fields should be prepared
      if (preparePanelP) {
         preparePanelP = false;
         PPDSP_INZ_prepare();
      }
   }

   /**
   * P-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PPDSP_INZ_prepare() {
      // Opening panel
      DSP.WWSPIC = pSettings.SPIC.get().getChar();
      // Default panel sequence
      DSP.WWDSEQ.move(pSettings.DSEQ.get());
   }

   /**
   * P-panel - handle F14.
   */
   public void PPDSP_F14() { 	
      //   Create program Meta data 	
      CRMNGVW.createStandardMetaData(LDAZD.CONO, "CrtPgm"); 	
      XXQTTP = -9; 	 	
      XXPAVR.setMax(); 	
      picSetMethod('I'); 	
      return; 	
   }

   /**
   * P-panel - check.
   */
   public void PPCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, settings - step validate
      // =========================================
      pSettings.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PPCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelP = true;
      // =========================================
      // Handle messages
      handleMessages(pSettings.messages, 'P', false);
      if (pSettings.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pSettings.release();
   }

   /**
   * P-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PPCHK_prepare() {
      // Opening panel
      if (pSettings.SPIC.isAccessMANDATORYorOPTIONAL()) {
         pSettings.SPIC.set().move(DSP.WWSPIC);
      }
      // Default panel sequence
      if (pSettings.DSEQ.isAccessMANDATORYorOPTIONAL()) {
         pSettings.DSEQ.set().move(DSP.WWDSEQ);
         if (!seqCheckSeq(getAllowedPanels(), DSP.WWDSEQ)) {
            return false;
         }
      }
      return true;
   }

   /**
   * P-panel - update.
   */
   public void PPUPD() {
      // Declaration
      boolean error = false;
      // Call APS450Fnc, settings - step update
      // =========================================
      pSettings.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      preparePanelP = true;
      // =========================================
      // Handle messages
      error = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
      // Next step
      if (!error) {
         picSetStack(2, DSP.WWSPIC, 'I');
         DSP.WWPSEQ.move(DSP.WWDSEQ);
         this.CSSQ.moveLeft(this.SEQ);
      }
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_P(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("SPIC")) {  DSP.setFocus("WWSPIC");
      } else if (FLDI.EQ("DSEQ")) {  DSP.setFocus("WWDSEQ");
      }
   }

   /**
   * Returns Payee if not blank, otherwise return Supplier
   * @param SPYN Payee
   * @param SUNO Supplier
   * @return Payee if not blank, otherwise return Supplier
   */
   public MvxString retrieveSUNO(MvxString SPYN, MvxString SUNO) {
      if (!SPYN.isBlank()) {
         return SPYN;
      } else {
         return SUNO;
      }
   }

   /**
   * T - panel.
   * Handle program CRS980 - Work with text
   */
   public void PTMAIN() {
      APIBH.CHAIN("00", APIBH.getKey("00"));
      if (F06 == DSP.X0FKEY) {
         DSP.restoreFocus();
      }
      this.PXCONO = APIBH.getCONO();
      this.PXDIVI.clear(); 	
      this.PXFTXH.moveLeft("FSYTXH00");
      this.PXFTXL.moveLeft("FSYTXL00");
      this.PXKFLD.moveLeft(APIBH.getINBN(), cRefINBN.length());
      this.PXFILE.moveLeft("FAPIBH00");
      this.PXTXID = APIBH.getTXID();
      this.PXLNCD.clear();
      this.PXTXVR.clear();
      this.PXPICC.moveLeft("EI");
      if (CRCommon.getMode() == cEnumMode.RETRIEVE) {
         this.PXOPT2.move(" 5");
      } else {
         this.PXOPT2.move(" 2");
      }
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      // CALL=CRS980 Text. Open
      PXCRS98X.CRS980();
      LDAZZ.FPNM.move(XFPGNM);
      if (APIBH.getTXID() != this.PXTXID) {
         if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
            APIBH.setTXID(this.PXTXID);
            APIBH.UPDAT("00");
         }
      }
      if (F06 == DSP.X0FKEY) {
         picPop();
      } else {
         // Next step
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
         } else {
            picSetMethod('D');
            picPush(seqSwitchToNextPanel(), 'I');
         }
      }
   }

   /**
   * Jump to program APS451 - Supplier invoice batch. Open lines
   */
   public void P1MAIN() {
      this.PXPGNM.moveLeftPad("APS451");
      COMSTK();
      if (this.PXSTER == '1') {
         //   MSGID=XST0001 The program &1 is not now available due to a recursive call
         COMPMQ("XST0001", this.PXPGNM);
         seqPrev();
         picPop();
         return;
      }
      Bookmark savedBookmark = getBookmark(); 
      // Set primary key fields
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().move(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.setTRNO(0);
      String relatedKeyFields = getPrimaryKeyForTable("FAPIBL");
      setBookmark(
         /*program*/ "APS451",     
         /*panelSequence*/ "",
         /*includeStartPanel*/ false, 
         /*startPanel*/ 'B', 
         /*inquiryType*/ 1,                
         /*view*/ "",
         /*panel*/ ' ',            
         /*focusFieldName*/ "",
         /*tableName*/ "FAPIBL",
         /*keyFields*/ relatedKeyFields,
         /*startPanelFields*/ null,
         /*option*/ "");
      Bookmark newBookmark = getBookmark();
      // Call program       
      XFPGNM.move(LDAZZ.FPNM);      
      LDAZZ.PICC.clear();              
      LDAZZ.OPT2.clear();             
      LDAZZ.FPNM.moveLeftPad(DSPGM);         
      apCall("APS451");              
      LDAZZ.FPNM.move(XFPGNM);      
      setBookmark(savedBookmark);
      // Display bookmark error message with COMPMQ
      cCRCommon.issueBookmarkMessage(this, newBookmark);
      if (this.XXOPT2.NE("11")) {
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
            return;
         }
         //   Next step
         picSetMethod('D');
         picPush(seqSwitchToNextPanel(), 'I');
      } else {
         picPop();
      }
   }

   /**
   * Jump to program APS452 - Supplier invoice batch. Open Reject history
   */
   public void P2MAIN() {
      this.PXPGNM.moveLeftPad("APS452");
      COMSTK();
      if (this.PXSTER == '1') {
         //   MSGID=XST0001 The program &1 is not now available due to a recursive call
         COMPMQ("XST0001", this.PXPGNM);
         seqPrev();
         picPop();
         return;
      }
      Bookmark savedBookmark = getBookmark(); 
      // Set primary key fields
      APIBR.setCONO(APIBH.getCONO());
      APIBR.setDIVI().move(APIBH.getDIVI());
      APIBR.setINBN(APIBH.getINBN());
      APIBR.setTRNO(0);
      String relatedKeyFields = getPrimaryKeyForTable("FAPIBR");
      setBookmark(
         /*program*/ "APS452",     
         /*panelSequence*/ "",
         /*includeStartPanel*/ false, 
         /*startPanel*/ 'B', 
         /*inquiryType*/ 1,                
         /*view*/ "",
         /*panel*/ ' ',            
         /*focusFieldName*/ "",
         /*tableName*/ "FAPIBR",
         /*keyFields*/ relatedKeyFields,
         /*startPanelFields*/ null,
         /*option*/ "");
      Bookmark newBookmark = getBookmark();
      // Call program       
      XFPGNM.move(LDAZZ.FPNM);      
      LDAZZ.PICC.clear();              
      LDAZZ.OPT2.clear();             
      LDAZZ.FPNM.moveLeftPad(DSPGM);         
      apCall("APS452");              
      LDAZZ.FPNM.move(XFPGNM);      
      setBookmark(savedBookmark);
      // Display bookmark error message with COMPMQ
      cCRCommon.issueBookmarkMessage(this, newBookmark);
      if (this.XXOPT2.NE("12")) {
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
            return;
         }
         //   Next step
         picSetMethod('D');
         picPush(seqSwitchToNextPanel(), 'I');
      } else {
         picPop();
      }
   }

   /**
   * Jump to program PPS127 - Generate Supplier Claim Invoice
   */
   public void P3MAIN() {
      this.PXPGNM.moveLeftPad("PPS127");
      COMSTK();
      if (this.PXSTER == '1') {
         //   MSGID=XST0001 The program &1 is not now available due to a recursive call
         COMPMQ("XST0001", this.PXPGNM);
         seqPrev();
         picPop();
         return;
      }
      Bookmark savedBookmark = getBookmark(); 
      // Set primary key fields
      PSUPR.setCONO(APIBH.getCONO());
      PSUPR.setDIVI().moveLeftPad(APIBH.getDIVI());
      PSUPR.setINBN(APIBH.getINBN());
      // Get first record in MPSUPR for INBN
      if (!PSUPR.CHAIN("50", PSUPR.getKey("50", 3))) {
         //   Next step
         if (this.XXOPT2.NE("13")) {
            picSetMethod('D');
            picPush(seqSwitchToNextPanel(), 'I');
         } else {
            picPop();
         }
         return;
      }
      String relatedKeyFields = getPrimaryKeyForTable("MPSUPR");
      setBookmark( // The following values are set in CRS014: panelSequence, inquiry type, view (defaulted via PPS127Md.programStartValues)
         /*program*/ "PPS127",     
         /*panelSequence*/ "",
         /*includeStartPanel*/ false, 
         /*startPanel*/ 'B', 
         /*inquiryType*/ 0,                
         /*view*/ "",
         /*panel*/ ' ',            
         /*focusFieldName*/ "",
         /*tableName*/ "MPSUPR",
         /*keyFields*/ relatedKeyFields,
         /*startPanelFields*/ null,
         /*option*/ "");
      Bookmark newBookmark = getBookmark();
      // Call program       
      XFPGNM.move(LDAZZ.FPNM);      
      LDAZZ.PICC.clear();              
      LDAZZ.OPT2.clear();             
      LDAZZ.FPNM.moveLeftPad(DSPGM);         
      apCall("PPS127");              
      LDAZZ.FPNM.move(XFPGNM);      
      setBookmark(savedBookmark);
      // Display bookmark error message with COMPMQ
      cCRCommon.issueBookmarkMessage(this, newBookmark);
      // Next step
      if (this.XXOPT2.NE("13")) {
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
            return;
         }
         picSetMethod('D');
         picPush(seqSwitchToNextPanel(), 'I');
      } else {
         picPop();
      }
   }

   /**
   * Jump to program CMS421 - Display error log
   */
   public void P4MAIN() {
      this.PXPGNM.clear();
      this.PXPGNM.moveLeft("CMS421");
      COMSTK();
      if (this.PXSTER == '1') {
         // MSGID=XST0001 The program &1 is not now available due to a recursive c
         COMPMQ("XST0001", formatToString(this.PXPGNM));
         seqPrev();
         picPop();
         return;
      }
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      LDAZZ.PICC.move("BI");
      LDAZZ.TDTA.clear();
      LDAZZ.TDTA.moveLeftPad(APIBH.getDTID(), 13);
      LDAZZ.OPT2.clear();
      apCall("CMS421");
      LDAZZ.FPNM.move(XFPGNM);
      if (this.XXOPT2.NE("14")) {
         if (F12 == LDAZZ.FKEY) {
            // F12=Previous
            seqPrev();
            picPop();
            return;
         }
         // Next step
         picSetMethod('D');
         picPush(seqSwitchToNextPanel(), 'I');
      } else {
         picPop();
      }
   }

   /**
   * Roll detail panel.
   * Switches to new record on the detail panels.
   * @param DIVI
   *    The current value.
   * @param INBN
   *    The current value.
   * @param fieldName
   *    The name of the display field to set focus on in error message.
   */
   public void roll(MvxString DIVI, long INBN, String fieldName) {
      MvxRecord save2_FAPIBH = null;
      int numberOfRecords = 0;
      int maxRecords = 0;
      // Determine what to do
      boolean rollForwards = (FROLLU == DSP.X0FKEY || F08 == DSP.X0FKEY);
      boolean rollBackwards = (FROLLD == DSP.X0FKEY || F07 == DSP.X0FKEY);
      boolean switchRecord = false;
      if (!rollForwards && !rollBackwards) {
         switchRecord = APIBH.getDIVI().NE(DIVI) || APIBH.getINBN() != INBN;
      }
      // Roll
      if (rollForwards || rollBackwards) {
         maxRecords = CRMNGVW.getMaxRecords(SYVIU);
         // Save record
         save2_FAPIBH = APIBH.getEmptyRecord();
         save2_FAPIBH.setRecord(APIBH);
         // Position for next record - SETGT or SETLL
         if (IN70 || IN67 || IN68) {
            mainTableSelection = CRMNGVW.setSelection(mainTableSelection, SYIBC.getFILE(), SYVIU.getSLF1(), SYVIU.getSLF2(), SYVIU.getSLF3(), SYSOR);
         }
         if (rollForwards) {
            PXAOPT.move("SETGT");
         } else {
            PXAOPT.move("SETLL");
         }
         PXAKNO = CRMNGVW.getNumberOfKeyFields();
         accessTable();
         // Read record
         if (rollForwards) {
            PXAOPT.move("READE");
         } else  {
            PXAOPT.move("REDPE");
         }
         PXAKNO = CRMNGVW.getNumberOfFilterFields();
         accessTable();
         while (!IN93) {
            if (isValidLineAuthority()) {
               // A record is found
               break;
            }
            // Read record
            if (rollForwards) {
               PXAOPT.move("READE");
            } else  {
               PXAOPT.move("REDPE");
            }
            PXAKNO = CRMNGVW.getNumberOfFilterFields();
            accessTable();
            numberOfRecords++;
            if (maxRecords < numberOfRecords) {
               IN93 = true;
            }
         }
         if (!IN93) {
            // Display found record
            picSetMethod('I');
         } else {
            // Restore previous record
            APIBH.setRecord(save2_FAPIBH);
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus(fieldName);
            if (maxRecords < numberOfRecords) {
               // MSGID=XMX0801 The number of read records &1 is greater than the entere
               COMPMQ("XMX0801", formatToString(numberOfRecords, 15));
            } else {
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
            }
         }
         return;
      }
      // Switch record
      if (switchRecord) {
         // Save record
         save2_FAPIBH = APIBH.getEmptyRecord();
         save2_FAPIBH.setRecord(APIBH);
         // Get new record
         APIBH.setDIVI().moveLeftPad(DIVI);
         APIBH.setINBN(INBN);
         if (APIBH.CHAIN("00", APIBH.getKey("00"))) {
            if (isValidLineAuthority()){
               // Display selected record
               picSetMethod('I');
               return;
            } else {
               // Restore record
               APIBH.setRecord(save2_FAPIBH);
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus(fieldName);
               // MSGID=XAU0007 Not authorized
               COMPMQ("XAU0007");
               return;
            }
         }
         // Restore record
         APIBH.setRecord(save2_FAPIBH);
         picSetMethod('D');
         IN60 = true;
         DSP.setFocus(fieldName);
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         COMPMQ("WINBN03", formatToString(INBN, cRefINBN.length()));
         return;
      }
      // Switch to next panel
      picSetMethod('I');
      picPush(seqSwitchToNextPanel(), 'I');
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @param panel
   *    The current panel.
   * @param suppressNotification
   *    Indicates if notification messages should be suppressed.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean handleMessages(cCRMessageList messages, char panel, boolean suppressNotification) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      boolean clearOption = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         while (messages.getNextError(CRMessageDS)) {
            if (panel != 'E' && panel != 'F' && panel != 'G' && panel != 'K' && panel != 'L' && panel != 'M' && panel != 'N'
                || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                || panel == 'F' && isFieldOnPanel_F(CRMessageDS.getPXFLDI())
                || panel == 'G' && isFieldOnPanel_G(CRMessageDS.getPXFLDI())
                || panel == 'K' && isFieldOnPanel_K(CRMessageDS.getPXFLDI())
                || panel == 'L' && isFieldOnPanel_L(CRMessageDS.getPXFLDI())
                || panel == 'M' && isFieldOnPanel_M(CRMessageDS.getPXFLDI())
                || panel == 'N' && isFieldOnPanel_N(CRMessageDS.getPXFLDI())
               )
            {
               error = true;
               break;
            }
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            while (messages.getNextNotification(CRMessageDS)) {
               if (panel != 'E' && panel != 'F' && panel != 'G' && panel != 'K' && panel != 'L' && panel != 'M' && panel != 'N'
                   || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                   || panel == 'F' && isFieldOnPanel_F(CRMessageDS.getPXFLDI())
                   || panel == 'G' && isFieldOnPanel_G(CRMessageDS.getPXFLDI())
                   || panel == 'K' && isFieldOnPanel_K(CRMessageDS.getPXFLDI())
                   || panel == 'L' && isFieldOnPanel_L(CRMessageDS.getPXFLDI())
                   || panel == 'M' && isFieldOnPanel_M(CRMessageDS.getPXFLDI())
                   || panel == 'N' && isFieldOnPanel_N(CRMessageDS.getPXFLDI())
                  )
               {
                  messages.rememberNotification(CRMessageDS.getPXMSID().toStringRTrim());
                  notification = true;
                  break;
               }
            }
         }
         // Issue message
         if (error || notification) {
            if (error && lastPanel =='B') {
               // Display message in B-panel
               switch (panel) {
                  case 'B':
                     clearOption = true;
                     break;
                  case 'C':
                     clearOption = true;
                     PCDSP_F12();
                     break;
                  case 'D':
                     clearOption = true;
                     PDDSP_F12();
                     break;
                  case 'E':
                     clearOption = true;
                     PEDSP_F12();
                     break;
                  case 'F':
                     clearOption = true;
                     PFDSP_F12();
                     break;
                  case 'G':
                     clearOption = true;
                     PGDSP_F12();
                     break;
                  case 'K':
                     clearOption = true;
                     PKDSP_F12();
                     break;
                  case 'L':
                     clearOption = true;
                     PLDSP_F12();
                     break;
                  case 'M':
                     clearOption = true;
                     PMDSP_F12();
                     break;
                  case 'N':
                     clearOption = true;
                     PNDSP_F12();
                     break;
                  case 'P':
                     PPDSP_F12();
                     break;
               }
            } else {
               // Display message in detail panel
               switch (panel) {
                  case 'C':
                     setFocusOnPanel_C(CRMessageDS.getPXFLDI());
                     break;
                  case 'D':
                     setFocusOnPanel_D(CRMessageDS.getPXFLDI());
                     break;
                  case 'E':
                     setFocusOnPanel_E(CRMessageDS.getPXFLDI());
                     break;
                  case 'F':
                     setFocusOnPanel_F(CRMessageDS.getPXFLDI());
                     break;
                  case 'G':
                     setFocusOnPanel_G(CRMessageDS.getPXFLDI());
                     break;
                  case 'K':
                     setFocusOnPanel_K(CRMessageDS.getPXFLDI());
                     break;
                  case 'L':
                     setFocusOnPanel_L(CRMessageDS.getPXFLDI());
                     break;
                  case 'M':
                     setFocusOnPanel_M(CRMessageDS.getPXFLDI());
                     break;
                  case 'N':
                     setFocusOnPanel_N(CRMessageDS.getPXFLDI());
                     break;
                  case 'P':
                     setFocusOnPanel_P(CRMessageDS.getPXFLDI());
                     break;
               }
            }
            if (error || notification && !suppressNotification) {
               CRCommon.COMPMQ(CRMessageDS);
            }
         } else if (messages.overflow()){
            // Didn't find any message relating to the given panel due to message overflow
            error = true;
            // MSGID=X_00035 Couldn't validate the panel due to many messages on other panels
            COMPMQ("X_00035");
         }
         if (error || notification && !suppressNotification) {
            if (clearOption && DSP.WBRRNA != 0) {
               moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
               DSP.clearOption();
               DSP.XBRRNA = DSP.WBRRNA;
               if (DSP.S0NAGG > 1) {
                  // Reverse image on aggregated line
                  IN66 = true;
               }
               DSP.updateSFL("BS");
               IN66 = false;
               setFocus_on_subfile_B(DSP.WBRRNA);
            }
            picSetMethod('D');
            IN60 = true;
         }
      }
      return error;
   }

  /**
  * Calls APS455 with a bookmark - setup for displaying the E-panel.
  * @param DIVI
  *    Division. Use this to preset DIVI in APS455.
  * @param INBN
  *    Invoice batch number. Use to preset INBN in APS455.
  * @param IBOP
  *    Invoice batch operation. Use to preset IBOP in in APS455.
  * @return
  *    False if the bookmark call failed with an error message.
  */
  public boolean callAPS455(MvxString DIVI, long INBN, int IBOP) {
      boolean error = false;
      this.PXPGNM.moveLeftPad("APS455");
      COMSTK();
      if (this.PXSTER == '1') {
         // MSGID=XST0001 The program &1 is not now available due to a recursive c
         COMPMQ("XST0001", formatToString(this.PXPGNM));
         return false;
      }
      Bookmark savedBookmark = getBookmark(); 
      // Set primary key fields
      SYSTP.setCONO(APIBH.getCONO());
      SYSTP.setDIVI().move(LDAZD.DIVI);
      SYSTP.setPGNM().moveLeftPad("APS455");
      SYSTP.setRESP().moveLeftPad(this.DSUSS);
      SYSTP.setLIVR().moveLeftPad(this.DSUSS);
      String keyFields = getPrimaryKeyForTable("CSYSTP");
      // Create bookmark to E panel
      setBookmark(
         /*program*/ "APS455",
         /*panelSequence*/ "E",
         /*includeStartPanel*/ false,
         /*startPanel*/ 'B',
         /*inquiryType*/ 1,
         /*view*/ "",
         /*panel*/ 'E',
         /*focusFieldName*/ "",
         /*tableName*/ "CSYSTP",
         /*keyFields*/ keyFields,
         /*startPanelFields*/ null,
         /*option*/ " 2");
      // Set bookmark parameters
      Bookmark newBookmark = getBookmark();
      newBookmark.addParameter("presetDIVI", DIVI);
      newBookmark.addParameter("presetINBN", INBN);
      newBookmark.addParameter("presetIBOP", IBOP);
      // Call APS455
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.PICC.clear();
      LDAZZ.OPT2.clear();
      LDAZZ.FPNM.moveLeftPad(DSPGM);
      apCall("APS455");
      LDAZZ.FPNM.move(XFPGNM);
      setBookmark(savedBookmark);
      // Display bookmark error message with COMPMQ
      error = cCRCommon.issueBookmarkMessage(this, newBookmark);
      return !error;
  }

   /**
   *    Read / Position to record using cSRSOROPT
   */
   public void accessTable() {
      pAPIPGMpreCall();
      if (IN70 || IN67 || IN68) {
         SOROPT.call(APIBH, SYIBC.getFILE().toStringRTrim(), SYVIU.getSOPT().toString(), pAPIPGM, mainTableSelection);
      } else {
         SOROPT.call(APIBH, SYIBC.getFILE().toStringRTrim(), SYVIU.getSOPT().toString(), pAPIPGM);
      }
      pAPIPGMpostCall();
      IN91 = toBoolean(PXAI91);
      IN92 = toBoolean(PXAI92);
      IN93 = toBoolean(PXAI93);
   }

   /**
   * Refresh request sent by UI, called before the actual panel update request is sent
   * @param reason
   */
   public void refresh(String reason) { 	 	 	 	
      refreshReason.moveLeftPad(reason); 	 	 	 	
   } 	 	 	

   /**
   *    prepareListHead  - Create subfile heading
   */
   public void prepareListHead() {
      CRMNGVW.prepareListHead(DSP, "WWAGGR", SYSOR, SYIBC, SYVIU, FIFFD, SYKEY, 
         SYIBV, CM100, (DSP.WWNFTR + 1), DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, 
         DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, DSP.W9OBKV, 
         DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV, "W1OBKV", "W2OBKV", "W3OBKV", 
         "W4OBKV", "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV", singleDivision);   
      //  Create headings 	 	 	
      setOverridingHeading();
      CRMNGVW.heading();
      // Move resulting header to display and communicate the layout to UI 	 	 	 	
      DSP.S0SFH.moveLeft(CRMNGVW.SFL); 	 	 	 	 	 	 	 	
      CRMNGVW.calcSubfLayout(DSP, DSP.WWNFTR, DSP.WWAGGR, SYVIU.getAGRG(), SYSOR, 
         SYVIU.getSLF1(), SYVIU.getSLF2(), SYVIU.getSLF3(), "WFSLCT", "WTSLCT", 
         "WFSLC2", "WTSLC2", "WFSLC3", "WTSLC3", "W1OBKV", "W2OBKV", "W3OBKV", 
         "W4OBKV", "W5OBKV", "W6OBKV", "W7OBKV", "W8OBKV");
      CRMNGVW.sendColumnCondition(DSP); 	 	 	 	
      return; 	 	 	
   } 	 	 	

   /**
   *    set Overriding Heading  - set override heading (for &-fields and other...)
   */
   public void setOverridingHeading() { 	 	 	
   } 	 	 	

   /**
   *    set Overriding decimals  - set overriding decimals for  numeric fields
   */
   public void setOverridingDecimals() { 	
      // Purchase price
      CRMNGVW.setOverrideNoOfDecimals("E5CUAM", pMaintain.CUAM.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5VTAM", pMaintain.VTAM.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5TLNA", pMaintain.TLNA.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5TCHG", pMaintain.TCHG.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5TOPA", pMaintain.TOPA.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5CDC1", pMaintain.CDC1.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5CDC2", pMaintain.CDC2.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5CDC3", pMaintain.CDC3.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5TASD", pMaintain.TASD.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5TTXA", pMaintain.TTXA.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E5PRPA", pMaintain.PRPA.getDecimals());
   } 	 	 	

   /**
   *    set Virtual Field Values  - set values in virtual fields (&-fields)
   */
   public void setVirtualFieldValues() { 	 	 	
   
   } 	 	 	

   /**
   * Initiate new sorting order.
   *
   * @return false if error found.
   */
   public boolean initNewSortingOrder() {
      if (XXQTTP != DSP.WWQTTP) {
         CRMNGVW.saveFilterValues(SYSOR, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, 
               DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, DSP.WFSLCT, DSP.WTSLCT, 
               DSP.WFSLC2, DSP.WTSLC2, DSP.WFSLC3, DSP.WTSLC3);
         if (!CRMNGVW.setStandardFromSortingOrder(DSP, DSP.WWQTTP, "WWQTTP", SYVIU, SYVIP, SYSOR, 
                  blankIBCA, singleDivision)) {
            //Sorting order not found
            return false;
         }
         DSP.WWQTTP = SYVIU.getQTTP();
         DSP.WWNFTR = SYVIU.getNFTR();
         DSP.WWAGGR = SYVIU.getAGGR(); 	
         CRMNGVW.setDefaultValues(DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, 
               DSP.WWPSEQ, SYSPV, SYSPP, 'B', DSP.WWQTTP, startView, startPanelSequence);
         if (CRMNGVW.isJoinSorting(SYSOR.getJNSO())) {
            // Init Join sort info in MDB
            PXAOPT.move("SETLL");
            PXAKNO = 1;
            accessTable();
         }
         if (!CRMNGVW.initListHead(DSP, "WWNFTR", DSP.WWNFTR,  "WWQTTP", 
               SYSOR, FIFFD, SYKEY, APIBH, SYIBC.getFILE(), SYIBC.getMGRP(), SYVIU.getSOPT(), 
               SYVIU.getSLF1(), SYVIU.getSLF2(), SYVIU.getSLF3(), DSP.WXSLCT, DSP.WXSLC2, 
               DSP.WXSLC3, DSP.WXTXT1, DSP.WXTXT2, DSP.WXTXT3, DSP.WXTXT4, 
               DSP.WXTXT5, DSP.WXTXT6, DSP.WXTXT7, DSP.WXTXT8, DSP.W1OBKV, DSP.W2OBKV, 
               DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, 
               DSP.W9OBKV, DSP.WAOBKV, DSP.WBOBKV, DSP.WCOBKV, singleDivision, DSP.WFSLCT, 
               DSP.WTSLCT, "WFSLCT", "WTSLCT", DSP.WFSLC2, DSP.WTSLC2, "WFSLC2", "WTSLC2", 
               DSP.WFSLC3, DSP.WTSLC3, "WFSLC3", "WTSLC3")) {
               return false;
            // Sorting option does not exist or error on filter field
         }
         XBNFTR = DSP.WWNFTR;
         XBAGGR = DSP.WWAGGR;
         XXQTTP = DSP.WWQTTP;
      }
      return true;
   }

  /** 
   * Ensures that tables that are read via cRef-get methods are actually read in the next method call.
   * This is ensured by setting the found-flags to false.
   */
   public void ensureReadOfTables() {
      found_CIDMAS = false;
      found_CIDVEN = false;
      found_CSUDIV = false;
   }

   /** 
   * Set hidden key fields in subfile record
   */
   public void setHiddenFieldsInSubfileRecord() {
      // Set hidden key fields
      DSP.S0DIVI.moveLeftPad(CRMNGVW.saveCurrentKeyValueMvxString("E5DIVI", SYSOR));
      DSP.S0INBN = CRMNGVW.saveCurrentKeyValueLong("E5INBN", SYSOR);
   }

   /**
   * Processes the bookmark.
   * Prepares the program to process the bookmark with the option given in the bookmark.
   *
   * @return
   *    false if the program shall terminate after processing the bookmark.
   */
   public boolean processBookmark() { 	
      // Get bookmark 	
      Bookmark bookmark = getBookmark(); 	
      // Process bookmark 	
      if (bookmark.getTableName().EQ("FAPIBH")) { 	
         // Validate key values in the bookmark 	
         if(!bookmark.isValidIntField("CONO") ||
            !bookmark.isValidStringField("DIVI") ||
            !bookmark.isValidLongField("INBN")) 	
         { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	
         // Unpack key values from the bookmark 	
         bookmark_CONO = bookmark.getIntField("CONO");
         bookmark_DIVI.moveLeftPad(bookmark.getStringField("DIVI"));
         bookmark_INBN = bookmark.getLongField("INBN");
         // Check context of the bookmark 	
         if (bookmark_CONO != LDAZD.CONO) { 	
            //   MSGID=X_00026 You must log on to company &1 before using the bookmark 	
            bookmark.setError("X_00026", formatToString(bookmark_CONO, 3)); 	
            return false; 	
         } 	
         // Retrieve record 	
         APIBH.setCONO(bookmark_CONO);
         APIBH.setDIVI().move(bookmark_DIVI);         
         APIBH.setINBN(bookmark_INBN);
         found_FAPIBH = APIBH.CHAIN("00", APIBH.getKey("00"));
         if (!found_FAPIBH) { 	
            APIBH.clearNOKEY("00"); 	
            // Dummy save to make it possible to use the key values even though the record was not found
            saved_FAPIBH.setRecord(APIBH);
            APIBH.setRecord(saved_FAPIBH);
         }
         // Determine inquiry settings 	
         startPanel = bookmark.getStartPanel(picGetPanel()); 	
         startPanelSequence.move(bookmark.getPanelSequence(DSP.WWPSEQ)); 	
         startSortingOrder = bookmark.getInquiryType(DSP.WWQTTP);
         DSP.WWQTTP = startSortingOrder;
         if (!bookmark.getView().isBlank()) {
            startView.moveLeftPad(bookmark.getView());
         } 	
         if (!initNewSortingOrder()) {
            return false; 	
         }
         startPanelSequence.move(DSP.WWPSEQ);
         //   - Set filter and position fields based on table
         CRMNGVW.setListHeadFromTable(DSP, DSP.WOPAVR, DSP.WOUPVR, SYSPV, SYIBC, SYSOR, APIBH);
         // - Set filter and position fields based on bookmark parameters                              
         if (bookmark.parametersExist()) {                                                             
            // Entitlement number                                                                      
            if (bookmark.isValidStringParameter("XXSINO")) {                                           
               CRMNGVW.setListHeadField(DSP, bookmark.getStringParameter("XXSINO"), "E5SINO", SYSOR);
            }
         }   
         if (startPanel == 'B') { 	
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         }
      } else { 	
         // MSGID=X_00025 Invalid bookmark 	
         bookmark.setError("X_00025", ""); 	
         return false; 	
      } 	
      return true; 	
   } 	

   /**
   * Called to set key fields when processing bookmark in PBCHK.
   */
   public void setKeyFieldsInPBCHKFromBookmark() {
      APIBH.setDIVI().moveLeftPad(bookmark_DIVI);
      APIBH.setINBN(bookmark_INBN); 	
   } 	

   /**
   * Called in PBCHK to validate the selected record before processing it as a bookmark.
   */
   public void validateRecordInPBCHKInCaseOfBookmark() { 	
      // Set subfile hidden key fields from record. 	
      DSP.S0DIVI.moveLeftPad(APIBH.getDIVI());
      DSP.S0INBN = APIBH.getINBN();
      // Nothing to check 	
   } 	

   /**
   * Save start values.
   */
   public void SAVSTR() {
      // Update start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      SAVSTR_setValues();
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      pSettings.prepare(cEnumStep.VALIDATE);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (!pSettings.messages.existError()) {
         pSettings.prepare(cEnumStep.UPDATE);
         int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      }
      pSettings.release();
   }

   /**
   * Set start values before saving.
   */
   public void SAVSTR_setValues() {
      if (!bookmarkInSession(DSPGM)) {
         // Sorting order
         pSettings.QTTP.set(DSP.WWQTTP);
         // Filter fields
         CRMNGVW.saveStartFilters(DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, DSP.WFSLC3, 
                     DSP.WTSLC3, DSP.W1OBKV, DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, 
                     DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, SYSTR);
         pSettings.FSLP.set().moveLeftPad(SYSTR.getFSLP());
      } 	
   }

   /**
   * Set last record - End of program
   */
   public void SETLR() {
      // Clear data in RAM table CSYSPP 	 	
      SYSPP.setCONO(LDAZD.CONO); 	
      SYSPP.setPGNM().moveLeftPad(this.DSPGM); 	
      SYSPP.DELET("00", SYSPP.getKey("00", 2));
      // Clear data in RAM table CCM100 	
      CM100.setCONO(LDAZD.CONO);
      CM100.DELET("00", CM100.getKey("00", 1));
      // Clear data in RAM table CCM101 	
      CM101.setCONO(LDAZD.CONO);
      CM101.DELET("00", CM101.getKey("00", 1));
      DSP.clearDropDownList("WOPAVR"); 	 	 	
      DSP.clearDropDownList("WOUPVR"); 	
      DSP.clearDropDownList("WWQTTP");
      DSP.clearDropDownList("WEIBTP");
      DSP.clearDropDownList("WFIBTP");
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      if (pDelete != null) {
         pDelete.messages.forgetNotifications();
      }
      if (pCopy != null) {
         pCopy.messages.forgetNotifications();
      }
      if (pSettings != null) {
         pSettings.messages.forgetNotifications();
      }
      if (pChangeDivision != null) {
         pChangeDivision.messages.forgetNotifications();
      }
      if (pRejectInvoice != null) {
         pRejectInvoice.messages.forgetNotifications();
      }
      if (pAcknowledge != null) {
         pAcknowledge.messages.forgetNotifications();
      }
      if (pApproveInvoice != null) {
         pApproveInvoice.messages.forgetNotifications();
      }
      if (pReversePrintout != null) {
         pReversePrintout.messages.forgetNotifications();
      }
      if (pResetToStatusNew != null) {
         pResetToStatusNew.messages.forgetNotifications();
      }
      CRMNGVW.release();
      super.SETLR(true);
   }

   /**
   * Sets initial settings.
   * Note that some fields already get a default value by the function program.
   */
   public void setInitialSettings() {
      pSettings.SPIC.set().move(LDAZD.SPI1);
      pSettings.SPIC.set().move('B'); // Override SPI1, there is no A-panel in APS450
   }

   /**
   * Initiate program.
   */
   public void INIT() {
      if (saved_FAPIBH == null) {
         saved_FAPIBH = APIBH.getEmptyRecord();
      }
      initSubfile = true;
      // Init keys
      APIBH.setCONO(LDAZD.CONO);
      this.PXCONO = LDAZD.CONO; 	
      this.PXDIVI.clear(); 	
      // Check authority
      this.PXPGNM.move(this.DSPGM);
      this.PXAUPF.moveRight(LDAZD.AUPF);
      PXAUTCHK.CAUTCHK();
      this.PXO.move(this.PXALOP);
      // - Not allowed to run program
      if (this.PXALPG == 0) {
         // MSGID=XAU0002 You are not authorized to run the program &1
         SRCOMRCM.MSGLVL.moveLeft("*PRV");
         COMPMQ("XAU0002", formatToString(this.DSPGM));
         SETLR();
         return;
      }
      // Check start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (pSettings.messages.exists("XRE0103")) { // Record does not exist
         pSettings.prepare(cEnumStep.VALIDATE);
         setInitialSettings();
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         if (!pSettings.messages.existError()) {
            pSettings.prepare(cEnumStep.UPDATE);
            int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
            CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
         }
      }
      SYSTR.setFSLP().moveLeftPad(pSettings.FSLP.get());
      pSettings.release();
      // - Init panel sequence
      this.CSSQ.move(pSettings.DSEQ.get());
      seqLoad();
      // - Init start picture
      picSetPanel(pSettings.SPIC.get().getChar());
      picSetMethod('I');
      // - Init sorting order
      DSP.WWQTTP = pSettings.QTTP.get();
      XXQTTP = -9;
      //   Create program Meta data (if missing) and Create file CSYSPP and CCM101 in QTEMP 	 	
      if (CRMNGVW.initProgram(LDAZD.CONO, "ChkBildPgm", SYSPP, SYIBC, SYIBR, CM101, CM100, APIBH) != 0) {
         displaySubfile = false;
      }
      //  Retrieve program start values
      MMMNGINP.TXQTTP = DSP.WWQTTP;
      MMMNGINP.TXPGN1.move(this.DSPGM);
      MMMNGINP.TXPGN2.move(LDAZZ.FPNM);
      MMMNGINP.TXPSEQ.move(DSP.WWPSEQ);
      MMMNGINP.TXPAVR.clear();
      MMMNGINP.TXSPIC = picGetPanel();
      if (bookmarkInSession(DSPGM)) { 	
         Bookmark bookmark = getBookmark(); 	
         MMMNGINP.TXOPT2.move(bookmark.getOption());
      } else {
         if (!LDAZZ.FPNM.isBlank()) {
            MMMNGINP.TXOPT2.move(LDAZZ.OPT2);
         }
      } 	
      MMMNGINP.MMMNGINP();
      DSP.WWQTTP = MMMNGINP.TXQTTP;
      startSortingOrder = MMMNGINP.TXQTTP; 	
      DSP.WWPSEQ.move(MMMNGINP.TXPSEQ);
      startPanelSequence.move(MMMNGINP.TXPSEQ); 	
      startView.moveLeftPad(MMMNGINP.TXPAVR);
      // - Start panel
      if (MMMNGINP.TXSPIC == 'A' || MMMNGINP.TXSPIC == 'B') {
         picSetPanel(MMMNGINP.TXSPIC);
      } else {
         picSetPanel('B');
      }
      DSP.WWSPIC = picGetPanel();
      // - Check if detail panel should be selected
      if (bookmarkInSession(DSPGM)) {
         Bookmark bookmark = getBookmark();
         if ((bookmark.getOption().EQ(" 2") || bookmark.getOption().EQ(" 5")) &&
             MMMNGINP.TXSPIC != 'A' && 
             MMMNGINP.TXSPIC != 'B' &&
             bookmark.getPanel() == ' ')
         {
            bookmark.setPanel(MMMNGINP.TXSPIC);
         }
      }
      // Save start values if changed by MMMNGINP and not coming from a bookmark
      if (!bookmarkInSession(DSPGM) && MMMNGINP.TXSEOK == 1) {
         this.CSSQ.moveLeft(DSP.WWPSEQ); 	
         seqLoad(); 	
         // Save DSEQ and SPIC in CSYSTR
         pSettings = get_pSettings();
         pSettings.messages.forgetNotifications();
         pSettings.prepare(cEnumStep.INITIATE);
         pSettings.indicateAutomated();
         pSettings.RESP.set().move(LDAZD.RESP);
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         pSettings.prepare(cEnumStep.VALIDATE);
         pSettings.SPIC.set().move(DSP.WWSPIC);
         pSettings.DSEQ.set().move(DSP.WWDSEQ);
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         if (!pSettings.messages.existError()) {
            pSettings.prepare(cEnumStep.UPDATE);
            int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
            CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
         }
         pSettings.release();
      }
      blankIBCA.clear();
      // - Init Single division depending on central or local user
      singleDivision = 0;
      if (LDAZD.CMTP == 2 &&
          LDAZD.DIVI.isBlank()) {
      } else {
         singleDivision = 1;
      }
      // - Set dynamic drop down for Sorting order
      CRMNGVW.setDropDownForSorting(DSP, "WWQTTP", SYVIU, blankIBCA, singleDivision);
      // Check for bookmark
      if (bookmarkInSession(DSPGM)) { 	
         // Process bookmark 	
         if (!processBookmark()) { 	
            // Abort after processing the bookmark 	
            SETLR(); 	
            return; 	
         }
      } else {
         if (MMMNGINP.TXCFIB.EQ("*NODSP")) {
            initSubfile = false;
         }
         if (pSettings.QTTP.get() == DSP.WWQTTP) {
            // - Set filter fields from startvalues
            if (initNewSortingOrder()) {
               CRMNGVW.setStartFilters(DSP.WFSLCT, DSP.WTSLCT, DSP.WFSLC2, DSP.WTSLC2, DSP.WFSLC3, DSP.WTSLC3, DSP.W1OBKV, 
                        DSP.W2OBKV, DSP.W3OBKV, DSP.W4OBKV, DSP.W5OBKV, DSP.W6OBKV, DSP.W7OBKV, DSP.W8OBKV, SYSTR);
            }
         }
      } 	
   }

   String getApName() {
      return "APS450AP";
   }

   // Movex MDB definitions
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBL APIBL;
   public mvx.db.dta.FAPIBR APIBR;
   public mvx.db.dta.CIDMAS IDMAS;
   public mvx.db.dta.CIDVEN IDVEN;
   public mvx.db.dta.CSUDIV SUDIV;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.FAPRCD APRCD;
   public mvx.db.dta.CIADDR IADDR;
   public mvx.db.dta.CSYSPV SYSPV; 	 	
   public mvx.db.dta.CSYSPP SYSPP; 	 	
   public mvx.db.dta.CSYVIU SYVIU; 	 	 	
   public mvx.db.dta.CSYVIP SYVIP;
   public mvx.db.dta.CSYSOR SYSOR;
   public mvx.db.dta.CSYNBV SYNBV;
   public mvx.db.dta.CFUEXC FUEXC;
   public mvx.db.dta.MPSUPR PSUPR;
   public mvx.db.dta.CSYIBC SYIBC;
   public mvx.db.dta.CFIFFD FIFFD;
   public mvx.db.dta.CSYKEY SYKEY;
   public mvx.db.dta.CSYIBR SYIBR;
   public mvx.db.dta.CSYIBV SYIBV;
   public mvx.db.dta.CCM100 CM100;
   public mvx.db.dta.CCM101 CM101;
   public mvx.db.dta.FPPPAY PPPAY;
   public mvx.db.dta.FPLEDG PLEDG;
   public mvx.db.dta.CSYPAR SYPAR;
   public mvx.db.dta.MPHEAD PHEAD;
   public mvx.db.dta.CBANAC BANAC;
   public mvx.db.dta.CGEOJU GEOJU;
   public mvx.db.dta.MPAGRS PAGRS;
   // Movex MDB definitions end

   public void initMDB() {
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      APIBR = (mvx.db.dta.FAPIBR)getMDB("FAPIBR", APIBR);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
      IDVEN = (mvx.db.dta.CIDVEN)getMDB("CIDVEN", IDVEN);
      SUDIV = (mvx.db.dta.CSUDIV)getMDB("CSUDIV", SUDIV);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      APRCD = (mvx.db.dta.FAPRCD)getMDB("FAPRCD", APRCD);
      IADDR = (mvx.db.dta.CIADDR)getMDB("CIADDR", IADDR);
      SYSPV = (mvx.db.dta.CSYSPV)getMDB("CSYSPV", SYSPV); 	 	 	
      SYSPP = (mvx.db.dta.CSYSPP)getMDB("CSYSPP", SYSPP); 	 	 	 	
      SYVIU = (mvx.db.dta.CSYVIU)getMDB("CSYVIU", SYVIU); 	 	
      SYVIP = (mvx.db.dta.CSYVIP)getMDB("CSYVIP", SYVIP);
      SYSOR = (mvx.db.dta.CSYSOR)getMDB("CSYSOR", SYSOR);
      SYNBV = (mvx.db.dta.CSYNBV)getMDB("CSYNBV", SYNBV);
      FUEXC = (mvx.db.dta.CFUEXC)getMDB("CFUEXC", FUEXC);
      PSUPR = (mvx.db.dta.MPSUPR)getMDB("MPSUPR", PSUPR);
      SYIBC = (mvx.db.dta.CSYIBC)getMDB("CSYIBC", SYIBC);
      FIFFD = (mvx.db.dta.CFIFFD)getMDB("CFIFFD", FIFFD);
      SYKEY = (mvx.db.dta.CSYKEY)getMDB("CSYKEY", SYKEY);
      CM100 = (mvx.db.dta.CCM100)getMDB("CCM100", CM100);
      CM101 = (mvx.db.dta.CCM101)getMDB("CCM101", CM101);
      SYIBV = (mvx.db.dta.CSYIBV)getMDB("CSYIBV", SYIBV);
      SYIBR = (mvx.db.dta.CSYIBR)getMDB("CSYIBR", SYIBR);
      PPPAY = (mvx.db.dta.FPPPAY)getMDB("FPPPAY", PPPAY);
      PLEDG = (mvx.db.dta.FPLEDG)getMDB("FPLEDG", PLEDG);
      SYPAR = (mvx.db.dta.CSYPAR)getMDB("CSYPAR", SYPAR);
      PHEAD = (mvx.db.dta.MPHEAD)getMDB("MPHEAD", PHEAD);
      BANAC = (mvx.db.dta.CBANAC)getMDB("CBANAC", BANAC);
      GEOJU = (mvx.db.dta.CGEOJU)getMDB("CGEOJU", GEOJU);
      PAGRS = (mvx.db.dta.MPAGRS)getMDB("MPAGRS", PAGRS);
   }

   public void initDSP() {
      if (DSP == null) {
         DSP = new APS450DSP(this);
      }
   }

   public cPXAPS450FncINmaintain pMaintain = null;
   public cPXAPS450FncINdelete pDelete = null;
   public cPXAPS450FncINcopy pCopy = null;
   public cPXAPS450FncINsettings pSettings = null;
   public cPXAPS450FncINchangeDivision pChangeDivision = null;
   public cPXAPS450FncINrejectInvoice pRejectInvoice = null;
   public cPXAPS450FncINapproveInvoice pApproveInvoice = null;
   public cPXAPS450FncINacknowledge pAcknowledge = null;
   public cPXAPS450FncOPreversePrintout pReversePrintout = null;
   public cPXAPS450FncOPresetToStatusNew pResetToStatusNew = null;
   public cPXMNS920 PXMNS920 = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINmaintain get_pMaintain() {
      if (pMaintain == null) {
         cPXAPS450FncINmaintain newPlist = new cPXAPS450FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pMaintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pMaintain.allowUpdateWithErrors();
         return pMaintain;
      }
   }

   /**
   * Calling APS450Fnc with pMaintain as a transaction.
   */
   @Transaction(name=cPXAPS450FncINmaintain.LOGICAL_NAME)
   public void transaction_APS450FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINdelete get_pDelete() {
      if (pDelete == null) {
         cPXAPS450FncINdelete newPlist = new cPXAPS450FncINdelete();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pDelete.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pDelete.allowUpdateWithErrors();
         return pDelete;
      }
   }

   /**
   * Calling APS450Fnc with pDelete as a transaction.
   */
   @Transaction(name=cPXAPS450FncINdelete.LOGICAL_NAME)
   public void transaction_APS450FncINdelete() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINcopy get_pCopy() {
      if (pCopy == null) {
         cPXAPS450FncINcopy newPlist = new cPXAPS450FncINcopy();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pCopy.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pCopy.allowUpdateWithErrors();
         return pCopy;
      }
   }

   /**
   * Calling APS450Fnc with pCopy as a transaction.
   */
   @Transaction(name=cPXAPS450FncINcopy.LOGICAL_NAME)
   public void transaction_APS450FncINcopy() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINsettings get_pSettings() {
      if (pSettings == null) {
         cPXAPS450FncINsettings newPlist = new cPXAPS450FncINsettings();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pSettings.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pSettings.allowUpdateWithErrors();
         return pSettings;
      }
   }

   /**
   * Calling APS450Fnc with pSettings as a transaction.
   */
   @Transaction(name=cPXAPS450FncINsettings.LOGICAL_NAME)
   public void transaction_APS450FncINsettings() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINchangeDivision get_pChangeDivision() {
      if (pChangeDivision == null) {
         cPXAPS450FncINchangeDivision newPlist = new cPXAPS450FncINchangeDivision();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pChangeDivision.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pChangeDivision.allowUpdateWithErrors();
         return pChangeDivision;
      }
   }

   /**
   * Calling APS450Fnc with pChangeDivision as a transaction.
   */
   @Transaction(name=cPXAPS450FncINchangeDivision.LOGICAL_NAME)
   public void transaction_APS450FncINchangeDivision() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pChangeDivision);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINrejectInvoice get_pRejectInvoice() {
      if (pRejectInvoice == null) {
         cPXAPS450FncINrejectInvoice newPlist = new cPXAPS450FncINrejectInvoice();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pRejectInvoice.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pRejectInvoice.allowUpdateWithErrors();
         return pRejectInvoice;
      }
   }

   /**
   * Calling APS450Fnc with pRejectInvoice as a transaction.
   */
   @Transaction(name=cPXAPS450FncINrejectInvoice.LOGICAL_NAME)
   public void transaction_APS450FncINrejectInvoice() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pRejectInvoice);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINapproveInvoice get_pApproveInvoice() {
      if (pApproveInvoice == null) {
         cPXAPS450FncINapproveInvoice newPlist = new cPXAPS450FncINapproveInvoice();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pApproveInvoice.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pApproveInvoice.allowUpdateWithErrors();
         return pApproveInvoice;
      }
   }

   /**
   * Calling APS450Fnc with pApproveInvoice as a transaction.
   */
   @Transaction(name=cPXAPS450FncINapproveInvoice.LOGICAL_NAME)
   public void transaction_APS450FncINapproveInvoice() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pApproveInvoice);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINacknowledge get_pAcknowledge() {
      if (pAcknowledge == null) {
         cPXAPS450FncINacknowledge newPlist = new cPXAPS450FncINacknowledge();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pAcknowledge.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pAcknowledge.allowUpdateWithErrors();
         return pAcknowledge;
      }
   }

   /**
   * Calling APS450Fnc with pAcknowledge as a transaction.
   */
   @Transaction(name=cPXAPS450FncINacknowledge.LOGICAL_NAME)
   public void transaction_APS450FncINacknowledge() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAcknowledge);
      LDAZZ.FPNM.move(XFPGNM);
   }

  /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPreversePrintout get_pReversePrintout() {
      if (pReversePrintout == null) {
         cPXAPS450FncOPreversePrintout newPlist = new cPXAPS450FncOPreversePrintout();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pReversePrintout.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pReversePrintout;
      }
   }

   /**
   * Calling APS450Fnc with pReversePrintout as a transaction.
   */
   @Transaction(name=cPXAPS450FncOPreversePrintout.LOGICAL_NAME)
   public void transaction_APS450FncOPreversePrintout() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pReversePrintout);
      LDAZZ.FPNM.move(XFPGNM);
   }

  /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPresetToStatusNew get_pResetToStatusNew() {
      if (pResetToStatusNew == null) {
         cPXAPS450FncOPresetToStatusNew newPlist = new cPXAPS450FncOPresetToStatusNew();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pResetToStatusNew.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pResetToStatusNew;
      }
   }

   /**
   * Calling APS450Fnc with pResetToStatusNew as a transaction.
   */
   @Transaction(name=cPXAPS450FncOPresetToStatusNew.LOGICAL_NAME)
   public void transaction_APS450FncOPresetToStatusNew() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pResetToStatusNew);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXMNS920 get_PXMNS920() {
      if (PXMNS920 == null) {
         cPXMNS920 newPlist = new cPXMNS920(this);
         return newPlist;
      } else {
         return PXMNS920;
      }
   }

   //*PARAM pAPIPGM{
   public MvxRecord pAPIPGM = new MvxRecord();// len = 1350

   public void pAPIPGMpreCall() {// insert param into record for call
      pAPIPGM.reset();
      pAPIPGM.set(PXAOPT);
      pAPIPGM.set(PXAI91);
      pAPIPGM.set(PXAI92);
      pAPIPGM.set(PXAI93);
      pAPIPGM.set(PXAKNO, 2);
      pAPIPGM.set(PXVAL2);
      pAPIPGM.set(PXVAL3);
      pAPIPGM.set(PXVAL4);
      pAPIPGM.set(PXVAL5);
      pAPIPGM.set(PXVAL6);
      pAPIPGM.set(PXVAL7);
      pAPIPGM.set(PXVAL8);
      pAPIPGM.set(PXVAL9);
      pAPIPGM.set(PXVA10);
      pAPIPGM.set(PXVA11);
      pAPIPGM.set(PXVA12);
      pAPIPGM.set(PXVA13);
      pAPIPGM.set(PXVA14);
      pAPIPGM.set(PXVA15);
      pAPIPGM.set(PXVA16);
   }

   public void pAPIPGMpostCall() {// extract param from record after call

      pAPIPGM.reset();
      pAPIPGM.getString(PXAOPT);
      PXAI91 = pAPIPGM.getChar();
      PXAI92 = pAPIPGM.getChar();
      PXAI93 = pAPIPGM.getChar();
      PXAKNO = pAPIPGM.getInt(2);
      pAPIPGM.getString(PXVAL2);
      pAPIPGM.getString(PXVAL3);
      pAPIPGM.getString(PXVAL4);
      pAPIPGM.getString(PXVAL5);
      pAPIPGM.getString(PXVAL6);
      pAPIPGM.getString(PXVAL7);
      pAPIPGM.getString(PXVAL8);
      pAPIPGM.getString(PXVAL9);
      pAPIPGM.getString(PXVA10);
      pAPIPGM.getString(PXVA11);
      pAPIPGM.getString(PXVA12);
      pAPIPGM.getString(PXVA13);
      pAPIPGM.getString(PXVA14);
      pAPIPGM.getString(PXVA15);
      pAPIPGM.getString(PXVA16);
   }

   public MvxRecord pPPRTVQTY = new MvxRecord();

   public void pPPRTVQTYpreCall() {
      pPPRTVQTY.reset();
      pPPRTVQTY.set(PPRTVQTYDS.getPPRTVQTYDS());
   }

   public void pPPRTVQTYpostCall() {
      pPPRTVQTY.reset();
      pPPRTVQTY.getString(PPRTVQTYDS.setPPRTVQTYDS());
   }

   public int bookmark_CONO;
   public MvxString bookmark_DIVI = cRefDIVI.likeDef();
   public long bookmark_INBN;
   public char startPanel; 	
   public MvxString startPanelSequence = cRefPSEQ.likeDef();
   public int startSortingOrder;
   public MvxString startView = cRefPAVR.likeDef();
   public GenericDef mainTableDef = null;
   public char PXAI93 = ' ';//*LIKE XXA1
   public char PXAI92 = ' ';//*LIKE XXA1
   public char PXAI91 = ' ';//*LIKE XXA1
   public int PXAKNO;//*LIKE XXN20
   public MvxString PXAOPT = new MvxString(5);//*LIKE XXA5
   public MvxString PXVA16 = cRefOPAR.likeDef();
   public MvxString PXVA15 = cRefOPAR.likeDef();
   public MvxString PXVA14 = cRefOPAR.likeDef();
   public MvxString PXVA13 = cRefOPAR.likeDef();
   public MvxString PXVA12 = cRefOPAR.likeDef();
   public MvxString PXVA11 = cRefOPAR.likeDef();
   public MvxString PXVA10 = cRefOPAR.likeDef();
   public MvxString PXVAL9 = cRefOPAR.likeDef();
   public MvxString PXVAL8 = cRefOPAR.likeDef();
   public MvxString PXVAL7 = cRefOPAR.likeDef();
   public MvxString PXVAL6 = cRefOPAR.likeDef();
   public MvxString PXVAL5 = cRefOPAR.likeDef();
   public MvxString PXVAL4 = cRefOPAR.likeDef();
   public MvxString PXVAL3 = cRefOPAR.likeDef();
   public MvxString PXVAL2 = cRefOPAR.likeDef();
   public MvxString PXVAL1 = cRefOPAR.likeDef();
   public MvxString X1OBKV = cRefOPAR.likeDef();
   public MvxString X2OBKV = cRefOPAR.likeDef();
   public MvxString X3OBKV = cRefOPAR.likeDef();
   public MvxString X4OBKV = cRefOPAR.likeDef();
   public MvxString X5OBKV = cRefOPAR.likeDef();
   public MvxString X6OBKV = cRefOPAR.likeDef();
   public MvxString X7OBKV = cRefOPAR.likeDef();
   public MvxString X8OBKV = cRefOPAR.likeDef();
   public MvxString X9OBKV = cRefOPAR.likeDef();
   public MvxString XAOBKV = cRefOPAR.likeDef();
   public MvxString XBOBKV = cRefOPAR.likeDef();
   public MvxString XCOBKV = cRefOPAR.likeDef();
   public MvxString XFSLCT = cRefSLCT.likeDef();
   public MvxString XTSLCT = cRefSLCT.likeDef();
   public MvxString XFSLC2 = cRefSLC2.likeDef();
   public MvxString XTSLC2 = cRefSLC2.likeDef();
   public MvxString XFSLC3 = cRefSLC3.likeDef();
   public MvxString XTSLC3 = cRefSLC3.likeDef();
   public MvxString fromDIVI = cRefDIVI.likeDef();
   public MvxString toDIVI = cRefDIVI.likeDef();
   public MvxString fromIBTP = cRefIBTP.likeDef();
   public MvxString toIBTP = cRefIBTP.likeDef();
   public MvxString fromSUPA = new MvxString(2);
   public MvxString toSUPA = new MvxString(2);
   public MvxString savedDIVI_CSUDIV = cRefDIVI.likeDef();
   public MvxString foundParam_CIDVEN = new MvxString(1);
   public MvxString foundParam_CSUDIV = new MvxString(1);
   public MvxString XFPGNM = cRefPGNM.likeDef();
   public cPXCRS98X PXCRS98X = new cPXCRS98X(this);
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);
   public boolean found_FAPIBH;
   public boolean passFAPIBH;
   public char lastPanel;
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public boolean errorOnInitiate;
   public cSRCOMPRI SRCOMPRI = new cSRCOMPRI(this);
   public cCRMNGVW CRMNGVW = new cCRMNGVW(this); 	 	 	
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public MvxString XXUPVR = cRefPAVR.likeDef();
   public MvxString XXPAVR = cRefPAVR.likeDef();
   public MvxString refreshReason = new MvxString(8);
   public int XXQTTP;
   public int sortingOrder;
   public int unique_lf_no;
   public boolean preparePanelC;
   public boolean preparePanelD;
   public boolean preparePanelE;
   public boolean preparePanelF;
   public boolean preparePanelG;
   public boolean preparePanelK;
   public boolean preparePanelL;
   public boolean preparePanelM;
   public boolean preparePanelN;
   public boolean preparePanelP;
   public boolean found_CIDMAS;
   public boolean found_CSUDIV;
   public boolean found_CIDVEN;
   public boolean found_CSYPAR_CRS750;
   public boolean found_CSYPAR_CRS793;
   public boolean found_CSYTAB_TDCD;
   public boolean found_MPAGRS;
   public boolean displaySubfile;
   public String unique_lf;
   public int XBNFTR;
   public int XBAGGR;
   public int singleDivision;
   public MvxString blankIBCA = cRefIBCA.likeDef();
   public cSRSOROPT SOROPT = new cSRSOROPT(this);
   public cMMMNGINP MMMNGINP = new cMMMNGINP(this);
   public sCRS750DS CRS750DS = new sCRS750DS(this);
   public sCRS793DS CRS793DS = new sCRS793DS(this);
   public MvxRecord saved_FAPIBH = null;
   public FieldSelection mainTableSelection;
   public MvxString sortingChanged = new MvxString(1);
   public MvxString savedIndicators31to40 = new MvxString(10);
   public MvxString savedIndicators61to90 = new MvxString(30);
   public MvxString savedLine = new MvxString(300);
   public cRef Ref = new cRef(this);
   public MvxString lastSPYN = cRefSPYN.likeDef();
   public boolean forceInitOfSubfile;
   public boolean initSubfile;
   public sPPRTVQTYDS PPRTVQTYDS = new sPPRTVQTYDS(this);
   public MvxString saveSEQ = new MvxString(this.SEQ.length());

   public APS450DSP DSP;

   public GenericDSP getDSP() {
      return (GenericDSP)DSP;
   }

   /**
   * Called when the program is recycled from the pool to clear class type fields within the program.
   */
   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(DSP);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(APIBR);
      v.addElement(IDMAS);
      v.addElement(IDVEN);
      v.addElement(SUDIV);
      v.addElement(MNDIV);
      v.addElement(SYTAB);
      v.addElement(APRCD);
      v.addElement(IADDR);
      v.addElement(SYSPV);
      v.addElement(SYSPP);
      v.addElement(SYVIU);
      v.addElement(SYVIP);
      v.addElement(SYSOR);
      v.addElement(SYNBV);
      v.addElement(FUEXC);
      v.addElement(PSUPR);
      v.addElement(SYPAR);
      v.addElement(PHEAD);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(pReversePrintout);
      v.addElement(pChangeDivision);
      v.addElement(pRejectInvoice);
      v.addElement(pApproveInvoice);
      v.addElement(pAcknowledge);
      v.addElement(pResetToStatusNew);
      v.addElement(PXMNS920);
      v.addElement(bookmark_DIVI);
      v.addElement(startPanelSequence);
      v.addElement(startView);
      v.addElement(XFPGNM);
      v.addElement(PXCRS98X);
      v.addElement(PLCHKAD);
      v.addElement(CRMessageDS);
      v.addElement(SRCOMPRI);
      v.addElement(CRMNGVW); 	 	 	 	
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(XXUPVR); 	 	 	
      v.addElement(XXPAVR); 	 	
      v.addElement(refreshReason);
      v.addElement(fromDIVI);
      v.addElement(toDIVI);
      v.addElement(fromIBTP);
      v.addElement(toIBTP);
      v.addElement(fromSUPA);
      v.addElement(toSUPA);
      v.addElement(savedDIVI_CSUDIV);
      v.addElement(foundParam_CIDVEN);
      v.addElement(foundParam_CSUDIV);
      v.addElement(unique_lf);
      v.addElement(SYIBC);
      v.addElement(FIFFD);
      v.addElement(SYKEY);
      v.addElement(X1OBKV);
      v.addElement(X2OBKV);
      v.addElement(X4OBKV);
      v.addElement(X3OBKV);
      v.addElement(X5OBKV);
      v.addElement(X6OBKV);
      v.addElement(X7OBKV);
      v.addElement(X8OBKV);
      v.addElement(X9OBKV);
      v.addElement(XAOBKV);
      v.addElement(XBOBKV);
      v.addElement(XCOBKV);
      v.addElement(PXAOPT);
      v.addElement(PXVA16);
      v.addElement(PXVA15);
      v.addElement(PXVA14);
      v.addElement(PXVA13);
      v.addElement(PXVA12);
      v.addElement(PXVA11);
      v.addElement(PXVA10);
      v.addElement(PXVAL9);
      v.addElement(PXVAL8);
      v.addElement(PXVAL7);
      v.addElement(PXVAL6);
      v.addElement(PXVAL5);
      v.addElement(PXVAL4);
      v.addElement(PXVAL3);
      v.addElement(PXVAL2);
      v.addElement(PXVAL1);
      v.addElement(SOROPT);
      v.addElement(XFSLCT);
      v.addElement(XTSLCT);
      v.addElement(XFSLC2);
      v.addElement(XTSLC2);
      v.addElement(XFSLC3);
      v.addElement(XTSLC3);
      v.addElement(blankIBCA);
      v.addElement(mainTableSelection);
      v.addElement(sortingChanged);
      v.addElement(CM100);
      v.addElement(CM101);
      v.addElement(SYIBV);
      v.addElement(SYIBR);
      v.addElement(MMMNGINP);
      v.addElement(savedIndicators31to40);
      v.addElement(savedIndicators61to90);
      v.addElement(savedLine);
      v.addElement(PPPAY);
      v.addElement(PLEDG);
      v.addElement(CRS750DS);
      v.addElement(CRS793DS);
      v.addElement(Ref);
      v.addElement(lastSPYN);
      v.addElement(BANAC);
      v.addElement(GEOJU);
      v.addElement(PAGRS);
      v.addElement(PPRTVQTYDS);
      v.addElement(saveSEQ);
      return version;
   }

   /**
   * Called when the program is recycled from the pool to clear primitive type fields within the program.
   */
   public void clearInstance() {
      super.clearInstance();
      bookmark_CONO = 0; 	
      startPanel = ' '; 	
      startSortingOrder = 0; 	
      found_FAPIBH = false;
      passFAPIBH = false;
      lastPanel = ' ';
      errorOnInitiate = false;
      XXQTTP = 0;
      sortingOrder = 0;
      preparePanelC = false;
      preparePanelD = false;
      preparePanelE = false;
      preparePanelF = false;
      preparePanelG = false;
      preparePanelK = false;
      preparePanelL = false;
      preparePanelM = false;
      preparePanelN = false;
      preparePanelP = false;
      found_CIDMAS = false;
      found_CSUDIV = false;
      found_CIDVEN = false;
      found_CSYPAR_CRS750 = false;
      found_CSYPAR_CRS793 = false;
      found_CSYTAB_TDCD = false;
      found_MPAGRS = false;
      bookmark_INBN = 0L;
      unique_lf_no = 0;
      displaySubfile = false;
      XBNFTR = 0;
      XBAGGR = 0;
      PXAKNO = 0;
      singleDivision = 0;
      saved_FAPIBH = null;
      PXAI91 = ' ';
      PXAI92 = ' ';
      PXAI93 = ' ';
      forceInitOfSubfile = false;
      initSubfile = false;
   }

   public String getBookmarkTableName() { 	
     return "FAPIBH"; 	
   } 	
   public static String[][] getBookmarkParameters() { 
     return bookmarkParameters; 
   }  
   public final static String[][] bookmarkParameters = {
      // PARAM       - The parameter identifier corresponding to a field in the program or view def (max 10 characters)
      // TYPE        - The parameter type: ALPHA, NUMERIC, or DATE
      // NAME        - The name of the parameter (max 15 characters)
      // DESCRIPTION - The purpose of the parameter (max 200 characters)
      //      
      //PARAM    TYPE       NAME               DESCRIPTION
      {"XXSINO", "ALPHA",   "Invoice number",  "Used for positioning on invoice number in the list view."},
   };
   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getPanelsRequiredForAdd() {
      return APS450Fnc.getPanelsRequiredForAdd();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getAllowedPanels() {
      return APS450Fnc.getAllowedPanels();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getDefaultPanels() {
      return APS450Fnc.getDefaultPanels();
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="58B2A888A6D74700B46569754303C17C";

public final static String _tempFixComment="";

public final static String _build="000000000000658";

public final static String _pgmName="APS450";

   public String getVersion() {
      return _version;
   } //·end of method getVersion

   public String getRelease() {
      return _release;
   } //·end of method getRelease

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   } //·end of method getBuild

   public String [][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
} 	
